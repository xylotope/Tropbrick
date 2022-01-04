;------------------------------------------------------------------------------------------------
; B3d Extension: XRef Scene support
;
;------------------------------------------------------------------------------------------------

Global EXT_ProjectRoot$   = ""
Global EXT_XrefSceneCount = 0

;------------------------------------------------------------------------------------------------
Function EXT_SetProjectRoot(root$)
;
; XRef files are referenced relative to a project root folder
;------------------------------------------------------------------------------------------------
	
	EXT_Log("SetProjectRoot: "+root$)
	If (Right$(root$,1)<>"\") Then root$ = root$ + "\"
	EXT_ProjectRoot$ = root$	

End Function

;------------------------------------------------------------------------------------------------
Function EXT_InitXrefScenes(ext.EXT_Entity,node)
;
; Recurse through hierarhy and find all XRef Scene nodes
;------------------------------------------------------------------------------------------------

	EXT_XrefSceneCount = 0
	EXT_Log( EXT_LogSection$ )
	EXT_Log("EXT_InitXrefScenes:")

	EXT_EnumXrefScenes(ext,node)

	EXT_Log("Total XRefScenes: " + EXT_XrefSceneCount)
		
End Function

;------------------------------------------------------------------------------------------------
Function EXT_EnumXrefScenes(ext.EXT_Entity,node)
;
; Recursively parse nodes in the hierarchy for XRefScene tags
;------------------------------------------------------------------------------------------------

	Local i,child
	
	For i = 1 To CountChildren(node)
 		child = GetChild(node,i)
		If (EXT_ParseTag(child,"B3D_XREFSCENE_")) Then EXT_LoadXrefScene(ext,child)
		If (CountChildren(child)) Then EXT_EnumXrefScenes(ext,child)
	Next
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_LoadXrefScene(ext.EXT_Entity,node)
;
;------------------------------------------------------------------------------------------------

	Local file$  = EntityName$(node)
 	Local entity = LoadAnimMesh(EXT_ProjectRoot$+file$,node)
	
	If (Not entity)
		EXT_Log("ERROR: Could not load Xref: "+EXT_ProjectRoot$+file$,1)
	Else
		EXT_XrefSceneCount = EXT_XrefSceneCount + 1
		EXT_Log("Loaded XrefScene: "+file$)
		EXT_InitInstances(ext,entity)	; make all instances
		EXT_EnumXrefScenes(ext,entity)	; load all xref scenes (recursive)
	EndIf

End Function
