;------------------------------------------------------------------------------------------------
; B3d Extensions: AutoHide
;
; AutoHide is a simple form of LOD. Similar to Blitz's native AutoFade(), 
; AutoHide hides an entity if it is not within a near/far range from the 
; render camera.
; 
; EXT_InitAutoHide(ext.EXT_Entity,node)	
; EXT_UpdateAutoHide(ext.EXT_Entity)	
; EXT_NumAutoHide(ext.EXT_Entity)		
; EXT_DeleteAutoHide(ext.EXT_Entity)
; EXT_DeleteAllAutoHide()
;
;------------------------------------------------------------------------------------------------

Type EXT_AutoHide
	
	Field entity						; Blitz Entity handle
	Field near2#						; near distance squared
	Field far2#							; far distance squared
	Field nextAutoHide.EXT_AutoHide		; linked list
	
End Type

;------------------------------------------------------------------------------------------------
Function EXT_InitAutoHide(ext.EXT_Entity,node)
	
	; make new B3d Extension
	Local hide.EXT_AutoHide = New EXT_AutoHide
	Local near# = EXT_ParseNum(node)
	Local far# 	= EXT_ParseNum(node)

	hide\entity = node
	hide\near2#	= near#*near#	
	hide\far2#	= far#*far#

	; add to AutoHide linked list 
	hide\nextAutoHide = ext\autoHide
	ext\AutoHide = hide
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_UpdateAutoHide(ext.EXT_Entity)
	
	Local hide.EXT_AutoHide = ext\autoHide
	Local x#,y#,z#,dist2#
	
	While (hide<>Null)
	
		x# = EntityX(hide\entity,1)-EntityX(EXT_RenderCam)
		y# = EntityY(hide\entity,1)-EntityY(EXT_RenderCam)
		z# = EntityZ(hide\entity,1)-EntityZ(EXT_RenderCam)
		dist2# = (x#*x#)+(y#*y#)+(z#*z#)
		
		If (dist2#>hide\near2# And dist2#<hide\far2#)
			ShowEntity(hide\entity)
		Else
			HideEntity(hide\entity)
		EndIf
		
		hide = hide\nextAutoHide		
	
	Wend
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_NumAutoHide(ext.EXT_Entity)
;
; returns the number of AutoHide controllers belonging to an EXT_Entity	
;------------------------------------------------------------------------------------------------

	Local hide.EXT_AutoHide = ext\autoHide
	Local count = 0

	While(hide <> Null)
		hide  = hide\nextAutoHide
		count = count + 1
	Wend
	
	Return count
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteAutoHide(ext.EXT_Entity)
;
; Deletes all AutoHide controllers belonging to an EXT_Entity
;------------------------------------------------------------------------------------------------

	Local hide.EXT_AutoHide = ext\autoHide
	Local old.EXT_AutoHide
	
	While(hide <> Null)
		old = hide
		hide = hide\nextAutoHide
		Delete old
	Wend

End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteAllAutoHide()
;
; Deletes all AutoHide controllers
	
	Delete Each EXT_AutoHide	
	
End Function

