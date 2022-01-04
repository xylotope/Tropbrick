;------------------------------------------------------------------------------------------------
; B3d Extensions: Occlude
;
; The Occlude extension hides occluded entities
;
; EXT_InitOcclude(ext.EXT_Entity,node)
; EXT_UpdateOcclude(ext.EXT_Entity)
; EXT_NumOcclude(ext.EXT_Entity)
; EXT_DeleteOcclude(ext.EXT_Entity)
; EXT_DeleteAllOcclude()
;
; NOTE: Occlude uses EXT_RenderCam
;
; TODO: Use an update timer to reduce/spread out the cost of the visibility checks
;
;------------------------------------------------------------------------------------------------

;------------------------------------------------------------------------------------------------
Type EXT_Occlude

	Field entity	; Blitz Entity handle
	Field nextOcclude.EXT_Occlude		; linked list

End Type

;------------------------------------------------------------------------------------------------
Function EXT_InitOcclude(ext.EXT_Entity,node)
	
	; make new B3d Extension
	Local occ.EXT_Occlude = New EXT_Occlude

	occ\entity  = node

	; add to ext Occlude linked list 
	occ\nextOcclude = ext\occlude
	ext\occlude = occ
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_UpdateOcclude(ext.EXT_Entity)
	
	Local occ.EXT_Occlude = ext\occlude
	
	While (occ<>Null)
	
   		If EntityVisible(occ\entity,EXT_RenderCam)
			ShowEntity(occ\entity)
		Else
 			HideEntity(occ\entity)
		EndIf
		
		occ = occ\nextOcclude
	
	Wend
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_NumOcclude(ext.EXT_Entity)
;
; Returns the number of Occlusion controllers belonging to an EXT_Entity
;------------------------------------------------------------------------------------------------

	Local occ.EXT_Occlude = ext\occlude
	Local count = 0
	
	While(occ <> Null)
		occ = occ\nextOcclude
		count = count + 1
	Wend

End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteOcclude(ext.EXT_Entity)
;
; Deletes all Occlusion controllers belonging to an EXT_Entity
;------------------------------------------------------------------------------------------------

	Local occ.EXT_Occlude = ext\occlude
	Local old.EXT_Occlude
	
	While(occ <> Null)
		old = occ
		occ = occ\nextOcclude
		Delete old
	Wend

End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteAllOcclude()
;
; Deletes all Occlusion controllers
;------------------------------------------------------------------------------------------------

	Delete Each EXT_Occlude

End Function