;------------------------------------------------------------------------------------------------
; B3d Extensions: Visibility
;
; The Visibility extension controls EntityAlpha (the visibility track in Max)
;
; EXT_InitVisibility(ext.EXT_Entity,node)
; EXT_UpdateVisibility(ext.EXT_Entity)
; EXT_NumVisibility(ext.EXT_Entity)
; EXT_DeleteVisibility(ext.EXT_Entity)
; EXT_DeleteAllVisibility()
;
;------------------------------------------------------------------------------------------------

;------------------------------------------------------------------------------------------------
Type EXT_Visibility

	Field entity	; Blitz Entity handle
	Field linkVis	; visibility controller node (local xpos = entity alpha)
	Field nextVisibility.EXT_Visibility		; linked list

End Type

;------------------------------------------------------------------------------------------------
Function EXT_InitVisibility(ext.EXT_Entity,node)
	
	; make new B3d Extension
	Local vis.EXT_Visibility = New EXT_Visibility

	vis\entity  = GetParent(node)
	vis\linkVis = node

	; add to ext Visibility linked list 
	vis\nextVisibility = ext\visibility
	ext\visibility = vis
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_UpdateVisibility(ext.EXT_Entity)
	
	Local vis.EXT_Visibility = ext\visibility
	Local alpha#
	
	While (vis<>Null)
	
		EntityAlpha(vis\entity,EntityX(vis\linkVis))	
		vis = vis\nextVisibility
	
	Wend
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_NumVisibility(ext.EXT_Entity)
;
; returns the number of Visibility controllers belonging to an EXT_Entity	
;------------------------------------------------------------------------------------------------

	Local vis.EXT_Visibility = ext\visibility
	Local count = 0

	While(vis <> Null)
		vis  = vis\nextVisibility
		count = count + 1
	Wend
	
	Return count
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteVisibility(ext.EXT_Entity)
;
; Deletes all Visibility controllers belonging to an EXT_Entity
;------------------------------------------------------------------------------------------------

	Local vis.EXT_Visibility = ext\visibility
	Local old.EXT_Visibility
	
	While(vis <> Null)
		old = vis
		vis = vis\nextVisibility
		Delete old
	Wend

End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteAllVisibility()
;
; Deletes all Visibility controllers
;------------------------------------------------------------------------------------------------

	Delete Each EXT_Visibility

End Function