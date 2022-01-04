;------------------------------------------------------------------------------------------------
; B3d Extension: AnimBrush
;
; B3d Brush Animation Extensions allows export of material animations from Max
; Currently supports color, alpha, and shininess.
;
; EXT_InitAnimBrush(ext.EXT_Entity,link)
; EXT_UpdateAnimBrushes(ext.EXT_Entity)
; EXT_NumAnimBrushes(ext.EXT_Entity)
; EXT_DeleteAnimBrushes(ext.EXT_Entity)
; EXT_DeleteAllAnimBrushes()
;
;------------------------------------------------------------------------------------------------

;------------------------------------------------------------------------------------------------
Type EXT_AnimBrush

	Field brush							; brush to animate
	Field linkColor						; color link node (x,y,z) = (r,g,b)
	Field linkAlpha						; alpha link node (xpos = alpha)
	Field linkShiny						; shininess link node (xpos = shininess)
	Field surface.EXT_Surface			; linked list of surfaces using the brush
	Field nextAnimBrush.EXT_AnimBrush 	; linked list 

End Type

Type EXT_Surface
	
	Field surface
	Field nextSurface.EXT_Surface
	
End Type

;------------------------------------------------------------------------------------------------
Function EXT_InitAnimBrush(ext.EXT_Entity,node)
;
; Creates an animated brush controller
;------------------------------------------------------------------------------------------------

 	Local anim.EXT_AnimBrush = New EXT_AnimBrush
	
 	anim\brush 		= GetEntityBrush(node)
	anim\linkColor 	= FindChild(node,"B3DEXT_COLOR")
	anim\linkAlpha 	= FindChild(node,"B3DEXT_ALPHA")
	anim\linkShiny 	= FindChild(node,"B3DEXT_SHINY")

	HideEntity(node)

 	EXT_FindAllBrushSurfaces(ext\root,anim)

	; add to EXT_Entity animbrush linked list
	anim\nextAnimBrush = ext\animBrush
	ext\animBrush = anim

End Function

;------------------------------------------------------------------------------------------------
Function EXT_FindAllBrushSurfaces(node,animBrush.EXT_AnimBrush)
;
; Finds and records all surfaces that use the specified brush
;------------------------------------------------------------------------------------------------

	Local i,child,surface
	Local newSurface.EXT_Surface
	Local testname$
	
	For i = 1 To CountChildren(node)

		child = GetChild(node,i)
		testname = EntityName(child)
			If (EntityClass$(child)="Mesh")

				surface = FindSurface(child,animBrush\brush)

				If (surface)

 					newSurface = New EXT_Surface
					newSurface\surface = surface
					; insert newSurf into linked list
					newSurface\nextSurface = animBrush\surface
					animBrush\surface = newSurface

				EndIf

			EndIf

		If(CountChildren(child)) Then EXT_FindAllBrushSurfaces(child,animBrush)

	Next

End Function

;------------------------------------------------------------------------------------------------
Function EXT_UpdateAnimBrushes(ext.EXT_Entity)
;
; Animates all EXT_AnimBrushes belonging to an EXT_Entity
;------------------------------------------------------------------------------------------------

	Local r#,g#,b#,a#
	Local shiny#
	Local brush,link
	Local paintSurf.EXT_Surface

	Local anim.EXT_AnimBrush = ext\animBrush

 	While (anim<>Null)

		brush = anim\brush

		If (anim\linkColor)
			
			link  = anim\linkColor
	
			r# = ( 255 * EntityX(link) ) Mod 256 
			g# = ( 255 * EntityZ(link) ) Mod 256 
			b# = ( 255 * EntityY(link) ) Mod 256 
 			BrushColor(brush,r#,g#,b#)
 		
 		EndIf

		If (anim\linkAlpha)
			
			a# = EntityX(anim\linkAlpha)
 			BrushAlpha(brush,a#)
			 		
		EndIf
 		
 		If (anim\linkShiny)
 			
 			shiny# = EntityX(anim\linkShiny)
 			BrushShininess(brush,shiny#)

 		EndIf
 		
 		; Paint all surfaces with the brush
		paintSurf = anim\surface
						
		While (paintSurf<>Null)
			PaintSurface(paintSurf\surface,anim\brush)
			paintSurf = paintSurf\nextSurface
		Wend

		anim = anim\nextAnimBrush
	
	Wend

End Function	

;------------------------------------------------------------------------------------------------
Function EXT_NumAnimBrushes(ext.EXT_Entity)
;
; Returns the number of animated brushes belonging to an EXT_Entity
;------------------------------------------------------------------------------------------------

	Local anim.EXT_AnimBrush = ext\animBrush
	Local count = 0

	While(anim <> Null)
		anim  = anim\nextAnimBrush
		count = count + 1
	Wend
	
	Return count
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteAnimBrushes(ext.EXT_Entity)
;
; Deletes all animated brush controllers belonging to an EXT_Entity
;------------------------------------------------------------------------------------------------

	Local anim.EXT_AnimBrush = ext\animBrush
	Local old.EXT_AnimBrush

	While(anim <> Null)
		old   = anim
		anim  = anim\nextAnimBrush
		Delete old
	Wend
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_DeleteAllAnimBrushes()
;
; Deletes all animated brush controllers
;------------------------------------------------------------------------------------------------

	Delete Each EXT_AnimBrush

End Function