;------------------------------------------------------------------------------------------------
; B3d Extension: AnimBrush
;
; B3d Brush Animation Extensions allows export of material animations
; such as Diffuse Color, etc. from Max.
;
; EXT_InitAnimMap(ext.EXT_Entity,link)
; EXT_UpdateAnimMaps(ext.EXT_Entity)
; EXT_NumAnimMaps(ext.EXT_Entity)
; EXT_DeleteAnimMaps(ext.EXT_Entity)
; EXT_DeleteAllAnimMaps()
;
;------------------------------------------------------------------------------------------------

;------------------------------------------------------------------------------------------------
Type EXT_AnimMap

	Field map							; map to animate
	Field linkUVpos						; UV position link node 	(x,y) 	= (u,v) 
	Field linkUVscale					; UV scale link node 		(x,y) 	= (u,v) 
	Field linkUVrot						; UV rotation link node 	(x) 	= rotation
	Field nextAnimMap.EXT_AnimMap	 	; linked list 

End Type

;------------------------------------------------------------------------------------------------
Function EXT_InitAnimMap(ext.EXT_Entity,node)
;
; Creates an animated map controller
;------------------------------------------------------------------------------------------------

 	Local brush = GetEntityBrush(node)
 	Local anim.EXT_AnimMap = New EXT_AnimMap
	Local index = EntityX(node)
	
 	anim\map 			= GetBrushTexture( brush,index )
 	anim\linkUVpos 		= FindChild(node,"B3DEXT_UVPOS")
 	anim\linkUVscale 	= FindChild(node,"B3DEXT_UVSCALE")
 	anim\linkUVrot	 	= FindChild(node,"B3DEXT_UVROT")

	HideEntity(node)

	; add to EXT_Entity animmap linked list
	anim\nextAnimMap = ext\animMap
	ext\animMap = anim

End Function

;------------------------------------------------------------------------------------------------
Function EXT_UpdateAnimMaps(ext.EXT_Entity)
;
; Animates all EXT_AnimMaps belonging to an EXT_Entity
;------------------------------------------------------------------------------------------------

	Local anim.EXT_AnimMap = ext\animMap
	Local map,link
	
 	While (anim<>Null)

		map  = anim\map
		
		If (anim\linkUVpos)
 			link = anim\linkUVpos
			PositionTexture(map,EntityX(link),EntityY(link))
		EndIf
			
		If (anim\linkUVscale)
 			link = anim\linkUVscale
			ScaleTexture(map,EntityX(link),EntityY(link))
			
		EndIf
		
		If (anim\linkUVrot)
			; Convert radians to degrees
			RotateTexture(map,EntityX(anim\linkUVrot)*57.2957795 )
		EndIf

		anim = anim\nextAnimMap
		
	Wend

End Function	

;------------------------------------------------------------------------------------------------
Function EXT_NumAnimMaps(ext.EXT_Entity)
;
; Returns the number of animated maps belonging to an EXT_Entity
;------------------------------------------------------------------------------------------------

	Local anim.EXT_AnimMap = ext\animMap
	Local count = 0

	While(anim <> Null)
		anim  = anim\nextAnimMap
		count = count + 1
	Wend
	
	Return count
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteAnimMaps(ext.EXT_Entity)
;
; Deletes all animated map controllers belonging to an EXT_Entity
;------------------------------------------------------------------------------------------------

	Local anim.EXT_AnimMap = ext\animMap
	Local old.EXT_AnimMap

	While(anim <> Null)
		old   = anim
		anim  = anim\nextAnimMap
		Delete old
	Wend
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteAllAnimMaps()
;
; Deletes all animated map controllers
;------------------------------------------------------------------------------------------------

	Delete Each EXT_AnimMap

End Function