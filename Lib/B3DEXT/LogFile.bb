;------------------------------------------------------------------------------------------------
; Log File Functions
;------------------------------------------------------------------------------------------------

;------------------------------------------------------------------------------------------------
Global EXT_LogFile$ = "B3dExtLog.txt"	; log file

;------------------------------------------------------------------------------------------------
Type EXT_LogMsg
	Field message$
End Type

;------------------------------------------------------------------------------------------------
Function EXT_Log(message$,level=2)
	
	DebugLog(message$)
	
	If (EXT_LogLevel>0)

	  	If (level>EXT_LogLevel) Then Return
		
	 	Local msg.EXT_LogMsg = New EXT_LogMsg
		msg\message$ = message$
	
	EndIf
		
End Function

;------------------------------------------------------------------------------------------------
Function EXT_WriteLogFile()
	
	If First EXT_LogMsg = Null Then Return 
	
	Local file = WriteFile(EXT_LogFile$)
	If (Not file) Then RuntimeError("Could not make log file: " + EXT_LogFile$)

	WriteLine(file, EXT_LogSection$)
	WriteLine(file,"B3d Extensions Log:")
	WriteLine(file, EXT_LogSection$)

	Local msg.EXT_LogMsg
	
	For msg = Each EXT_LogMsg
		WriteLine(file,msg\message$)
	Next
	
	CloseFile(file)
    ExecFile(EXT_LogFile$)
		
End Function



