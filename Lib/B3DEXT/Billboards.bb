;------------------------------------------------------------------------------------------------
; B3d Extension: Billboards
;
; Billboard controllers keep entities pointed at the current render camera (EXT_RenderCam)
;
; EXT_InitBillboard(ext.EXT_Entity,node)
; EXT_UpdateBillboards(ext.EXT_Entity,node)
; EXT_NumBillboards(ext.EXT_Entity)
; EXT_DeleteBillboards(ext.EXT_Entity)
; EXT_DeleteAllBillboards()
;
;------------------------------------------------------------------------------------------------

Const EXT_BB_NORMAL		= 1		
Const EXT_BB_VERTICAL	= 2		

;------------------------------------------------------------------------------------------------
Type EXT_Billboard

	Field entity		; entity to billboard
	Field class			; type of billboard
	Field nextBillboard.EXT_Billboard

End Type

;------------------------------------------------------------------------------------------------
Function EXT_InitBillboard(ext.EXT_Entity,node)
	
	; make new billboard controller
	Local bb.EXT_Billboard = New EXT_Billboard
	
	; init controller
	bb\entity 	= node			
	bb\class	= EXT_ParseNum(node)

	; add to ext billboard linked list 
	bb\nextBillboard = ext\billboard
	ext\billboard = bb
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_UpdateBillboards(ext.EXT_Entity)
	
	Local bb.EXT_Billboard = ext\billboard
	
	While (bb<>Null)
		PointEntity(bb\entity,EXT_RenderCam)
		If(bb\class = EXT_BB_VERTICAL) TurnEntity(bb\entity,-EntityPitch(bb\entity),0,0)
		bb = bb\nextBillboard
	Wend
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_NumBillboards(ext.EXT_Entity)
;
; Returns the number of billboards belonging to an EXT_Entity
;------------------------------------------------------------------------------------------------

	Local bb.EXT_Billboard = ext\billboard
	Local count = 0

	While(bb <> Null)
		bb  = bb\nextBillboard
		count = count + 1
	Wend
	
	Return count
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteBillboards(ext.EXT_Entity)
;
; Deletes all Billboard controllers belonging to an EXT_Entity
;------------------------------------------------------------------------------------------------

	Local bb.EXT_Billboard = ext\billboard
	Local old.EXT_Billboard
	
	While(bb <> Null)
		old = bb
		bb = bb\nextBillboard
		Delete old
	Wend

End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteAllBillboards()
;
; Deletes all Billboard controllers
;------------------------------------------------------------------------------------------------

	Delete Each EXT_Billboard

End Function