;------------------------------------------------------------------------------------------------
; B3d Extensions Library
; B3d Pipeline v0.6
;------------------------------------------------------------------------------------------------
;
; B3d Extensions allow you to export more scene data from Max than is 
; currently supported in the .b3d file format. The B3d Pipeline exporter
; uses a combination of naming conventions and dummy nodes to encode extra 
; data in a .b3d file. The B3d Pipeline documentation includes a detailed
; description of how extensions are saved in a b3d file.
;
; The B3d Extensions Library decodes this information and recreates the 
; original scene data. The library also builds and maintains "controllers" to 
; animate the extensions. 
; 
; Conventions:
;
; - All library functions and global variables have the prefix: EXT_
; - Global variables should be treated as private; avoid accessing them directly.
;
; Workflow:
;
; An EXT_Entity contains all the B3d Extensions in an entity's hierarchy.
;
; EXT_LoadEntity()    loads a b3d file and returns an EXT_Entity. 
; EXT_UpdateEntity()  updates all the B3d Extensions in an EXT_Entity.
; EXT_UpdateAll()     updates all EXT_Entity objects.
;
; RenderCam:
;
; Many extensions need a camera to work with (e.g. Billboards, Environment, etc.)
; These functions use the concept of an active RenderCam. You can set the
; render cam with EXT_SetRenderCam(camera).
;
;------------------------------------------------------------------------------------------------

;------------------------------------------------------------------------------------------------
Const EXT_LogLevel    = 1	; 0 = none ; 1 = errors only ; 2 = all messages
Const EXT_LogSection$ = "-----------------------------------------------------------------------"

;------------------------------------------------------------------------------------------------
Type EXT_Entity

	Field root							; Blitz entity handle
		
	; Controller linked lists
	Field animBrush.EXT_AnimBrush		; Controls animated brushes
	Field animMap.EXT_AnimMap			; Controls animated maps
	Field autoHide.EXT_AutoHide			; Controls hide/show at near/far ranges
	Field billboard.EXT_Billboard		; Controls entity facing to the active cam
	Field camera.EXT_Camera				; Controls cameras
	Field light.EXT_Light				; Controls lights
	Field linkPos.EXT_LinkPos			; Links position to render camera
	Field linkRot.EXT_LinkRot			; Links rotation to render camera
	Field occlude.EXT_Occlude			; Controls hide/show based on occlusion
	Field subAnim.EXT_SubAnim			; Controls sub-anims
	Field visibility.EXT_Visibility		; Controls entity alpha
	
	; Environment settings
	Field environment.EXT_Environment	; Controls environment settings 

End Type

;------------------------------------------------------------------------------------------------
Function EXT_LoadEntity.EXT_Entity(file$,parent=0)
;	
; Loads a B3d file and initializes b3d extensions
;------------------------------------------------------------------------------------------------
	
	EXT_Log( EXT_LogSection$ )
	EXT_Log("EXT_LoadEntity:")
	EXT_Log("File: " + file$)
	EXT_Log("Parent: " + parent)
	
	Local entity = LoadAnimMesh(file$,parent)

	If (Not entity)
		EXT_Log("ERROR: Could not load file!",1)
		Return Null
	EndIf
	
	ext.EXT_Entity = EXT_InitEntity(entity)

	Return ext
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_InitEntity.EXT_Entity(entity)
;	
; Scans a hierarchy for b3d extensions and initializes controllers
;------------------------------------------------------------------------------------------------
	
	EXT_Log( EXT_LogSection$ )
	EXT_Log("EXT_InitEntity:")

	If (Not entity)
		EXT_Log("ERROR: Invalid Entity!",1)
		Return Null
	EndIf
	
	Local ext.EXT_Entity = New EXT_Entity
	ext\root = entity
	
	EXT_InitInstances(ext,entity)	; resolve all instances
	EXT_InitXrefScenes(ext,entity)	; load all xref scenes (recursive)
	EXT_ParseNode(ext,entity)		; parse the root node
	EXT_EnumNodes(ext,entity)		; recurse through all children
	EXT_InitSubAnims(ext)			; find all animation roots
	
	EXT_WriteLogFile()
	
	Return ext
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_UpdateEntity(ext.EXT_Entity)
	
	EXT_UpdateSubAnims(ext)
	EXT_UpdateLights(ext)
	EXT_UpdateBillboards(ext)
	EXT_UpdateLinkPos(ext)
	EXT_UpdateLinkRot(ext)
	EXT_UpdateAutoHide(ext)
	EXT_UpdateVisibility(ext)
	EXT_UpdateOcclude(ext)
	EXT_UpdateAnimBrushes(ext)
	EXT_UpdateAnimMaps(ext)
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteEntity(ext.EXT_Entity)
	
	EXT_DeleteAnimBrushes(ext)
	EXT_DeleteAnimMaps(ext)
	EXT_DeleteAutoHide(ext)
	EXT_DeleteBillboards(ext)
	EXT_DeleteCameras(ext)
	EXT_DeleteLights(ext)
	EXT_DeleteLinkPos(ext)
	EXT_DeleteLinkRot(ext)
	EXT_DeleteOcclude(ext)
	EXT_DeleteSubAnims(ext)
	EXT_DeleteVisibility(ext)
	
	If (ext\environment <> Null) Then Delete ext\environment
		
	Delete ext
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_EnumNodes(ext.EXT_Entity,node)
;
; Recursively parse nodes in the hierarchy
;------------------------------------------------------------------------------------------------

	Local i,child
	
	For i = 1 To CountChildren(node)
		child = GetChild(node,i)
		EXT_ParseNode(ext,child)
		If(CountChildren(child)) Then EXT_enumNodes(ext,child)
	Next
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_ParseNode(ext.EXT_Entity,node)
;
; Parses the node and calls the appropriate init functions
;------------------------------------------------------------------------------------------------
	
	; Some B3d Extensions are defined by text tags in the node's name. 
	; These are generally static properties. Note: There can be multiple 
	; tags in a node's name, that's why the ParseNode() function is 
	; called again after finding a tag.
	
	If (EXT_ParseTag(node,"B3D_"))

		; Billboard
		If (EXT_ParseTag(node,"BB_"))
			EXT_InitBillboard(ext,node)
			EXT_ParseNode(ext,node)
			Return
		EndIf
		
		; AutoFade()
		If (EXT_ParseTag(node,"FADE_"))
			EXT_InitAutoFade(node)
			EXT_ParseNode(ext,node)
			Return
		EndIf

		; AutoHide
		If (EXT_ParseTag(node,"HIDE_"))
			EXT_InitAutoHide(ext,node)
			EXT_ParseNode(ext,node)
			Return
		EndIf

		; Link Pos to Render Cam
		If (EXT_ParseTag(node,"LPOS_"))
			EXT_InitLinkPos(ext,node)
			EXT_ParseNode(ext,node)
			Return
		EndIf

		; Link Rot to Render Cam
		If (EXT_ParseTag(node,"LROT_"))
			EXT_InitLinkRot(ext,node)
			EXT_ParseNode(ext,node)
			Return
		EndIf
		
		; EntityOrder()
		If (EXT_ParseTag(node,"ORDR_"))
			EXT_InitEntityOrder(node)
			EXT_ParseNode(ext,node)
			Return
		EndIf
		
		; EntityPickMode()
		If (EXT_ParseTag(node,"PICK_"))
			EXT_InitEntityPickMode(node)
			EXT_ParseNode(ext,node)
			Return
		EndIf
			
		; EntityType()
		If (EXT_ParseTag(node,"COLL_"))
			EXT_InitEntityType(node)
			EXT_ParseNode(ext,node)
			Return
		EndIf

		; Occlusion
		If (EXT_ParseTag(node,"OCC_"))
			EXT_InitOcclude(ext,node)
			EXT_ParseNode(ext,node)
			Return
		EndIf

		; Box
		If (EXT_ParseTag(node,"BOX_"))
			EXT_InitEntityBox(node)
			EXT_ParseNode(ext,node)
			Return
		EndIf

		; Radius
		If (EXT_ParseTag(node,"RADIUS_"))
			EXT_InitEntityRadius(node)
			EXT_ParseNode(ext,node)
			Return
		EndIf

	EndIf

	; Other B3d Extensions are defined by extra nodes in 
	; the hierarchy. These are generally animated extensions.

	Select EntityName$(node)

		Case "B3DEXT_CAMERA"
			EXT_InitCamera(ext,node)

		Case "B3DEXT_OMNILIGHT"
			EXT_InitLight(ext,node,EXT_OMNILIGHT)

		Case "B3DEXT_DIRLIGHT"
			EXT_InitLight(ext,node,EXT_DIRLIGHT)

		Case "B3DEXT_SPOTLIGHT"
			EXT_InitLight(ext,node,EXT_SPOTLIGHT)

		Case "B3DEXT_AMBIENT"
			EXT_InitAmbient(ext,node)

		Case "B3DEXT_BGCOLOR"
			EXT_InitBgColor(ext,node)

		Case "B3DEXT_FOGCOLOR"
			EXT_InitFogColor(ext,node)

		Case "B3DEXT_FOGRANGE"
			EXT_InitFogRange(ext,node)

		Case "B3DEXT_VISIBILITY"
			EXT_InitVisibility(ext,node)

		Case "B3DEXT_ANIMBRUSH"
			EXT_InitAnimBrush(ext,node)

		Case "B3DEXT_ANIMMAP"
			EXT_InitAnimMap(ext,node)
				
	End Select
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_ParseTag(node,tag$)
;
; checks for a B3d Extension tag in a node's name
;------------------------------------------------------------------------------------------------

	Local name$ = EntityName(node)
	
	If ( Left$(name$,Len(tag$)) = tag$ )

		name$=Right$(name$,Len(name$)-Len(tag$))
		NameEntity(node,name$)
		
		Return True
	
	EndIf
	
	Return False
		
End Function

;------------------------------------------------------------------------------------------------
Function EXT_ParseNum#(node)
;
; returns a numerical argument associated with a tag (e.g. B3D_FADE_100.000_200.000_)
;------------------------------------------------------------------------------------------------

	Local num#
	Local numChars  = 1
	Local name$ 	= EntityName(node)

	While (Mid$(name$,numChars,1)<>"_") numChars = numChars + 1: Wend

	num# = Left$(name$,numChars)

	name$ = Right$(name$,Len(name$)-numChars) 
	NameEntity(node,name$)
	
	Return num#
		
End Function
