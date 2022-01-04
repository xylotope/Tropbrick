;------------------------------------------------------------------------------------------------
; B3d Extension: Properties
;
; Sets various Blitz3d properties
;
; EXT_InitEntityOrder(node)
; EXT_InitAutoFade(node)
; EXT_InitEntityType(node)
; EXT_InitEntityPickMode(node)
; EXT_InitEntityBox(node)
; EXT_InitEntityRadius(node)
;
;------------------------------------------------------------------------------------------------

;------------------------------------------------------------------------------------------------
Function EXT_InitEntityOrder(node)

	Local order = EXT_ParseNum(node)
	EntityOrder(node,order)
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_InitAutoFade(node)

	Local near# = EXT_ParseNum(node)
	Local far# = EXT_ParseNum(node)
	EntityAutoFade(node,near#,far#)
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_InitEntityType(node)

	Local num = EXT_ParseNum(node)
	Local recursive = EXT_ParseNum(node)
	EntityType (node,num,recursive)
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_InitEntityPickMode(node)

	Local pickGeom = EXT_ParseNum(node)
	Local obscurer = EXT_ParseNum(node)
	EntityPickMode (node,pickGeom,obscurer)
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_InitEntityBox(node)
		
	Local xpos# 	=  EXT_ParseNum(node)
	Local zpos# 	=  EXT_ParseNum(node)
	Local ypos# 	=  EXT_ParseNum(node)
	Local width# 	=  EXT_ParseNum(node)
	Local depth# 	=  EXT_ParseNum(node)
	Local height# 	=  EXT_ParseNum(node)
	
	EntityBox(node,xpos#,ypos#,zpos#,width#,height#,depth#)
	
End Function

;------------------------------------------------------------------------------------------------
Function EXT_InitEntityRadius(node)

	Local xradius# 	=  EXT_ParseNum(node)
	Local yradius# 	=  EXT_ParseNum(node)
		
	EntityRadius(node,xradius#,yradius#)
	
End Function

