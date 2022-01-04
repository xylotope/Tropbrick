;
; "Juicy Fonts" User Function Library - v1.0
;
; Copyright (c) Chris Chadwick 2006.
;
; This file should be included in any application that uses bitmap
; fonts created with the Juicy Fonts editor.
;
; NOTE: The only error checking done in the functions of this library are
; in jf_load_font() to ensure the required files exist. All other errors,
; such as trying to display a deleted font, will result in a runtime error.
; This is intentional to highlight any errors in your code during development,
; just as all other blitz command errors do.
;

	; ------------------------
	; --- Type definitions ---
	; ------------------------

	Type jf_fontT
		Field font_image										; Image containing all font character frames.
		Field frame_height%									; Pixel height of all character frames.
		Field frame_widest%									; Pixel width of widest character frame.
		Field shad_dx%											; Horizontal pixel offset of shadow.
		Field ignore_shad%									; Flag: Ignore shadow when horizontally spacing characters?
		Field ignore_shad_w%								; 0 when shadow is ignored, else equals shadow width.
		Field shad_adjust%									; Opposite of ignore_shad_w.
		Field kern_align%										; Flag: Include left/rightmost kerning when aligning text?
		Field line_spacing%									; Vertical pixel spacing to use for multi-line text.
		Field mask_r%, mask_g%, mask_b%			; Font image mask color.
		Field char_metrics.jf_metricsT[255]	; Metrics of all characters defined in the font.
	End Type
	
	Type jf_metricsT
		Field frame_width%	; Pixel width of character's image.
		Field frame_x%			; x coord of character's frame within font image.
		Field frame_y%			; y coord of character's frame within font image.
		Field body_width%		; Frame width minus shadow width.
		Field kern_l%				; Number of pixels to kern on the left of the character.
		Field kern_r%				; Number of pixels to kern on the right of the character.
	End Type
	
	Type jf_textT
		Field font.jf_fontT	; Font to display text in.
		Field char_bank			; Bank holding position and frame of all text characters.
	End Type
	
	Type jf_lineT
		Field x%				; X coord of first character to be displayed.
		Field y%				; Y coord of all line characters.
		Field txt$			; Line text.
		Field width%		; Pixel width of text line.
		Field reach_l%	; Positional adjustment when char over-reaches first char of text line.
		Field reach_r%	; Positional adjustment when char over-reaches last char of text line.
	End Type


	; ---------------------------------
	; --- System global definitions ---
	; ---------------------------------

	; Return values from jf_text_size().
	Global jf_text_lines%
	Global jf_text_width%
	Global jf_text_height%
	

; ----------------------------------
; --- Juicy Fonts user functions ---
; ----------------------------------

;
; Specifies whether or not to include kerning when aligning
; the given font to specific screen coordinates.
;
; Params:
; font_h - Handle of font to be affected.
; state  - 'True' to include the font's kerning.
;          'False' to ignore the font's kerning.
;
Function jf_set_kern_align(font_h%, state%)

	f.jf_fontT = Object.jf_fontT(font_h)
	
	f\kern_align = (state <> False)

End Function


;
; Returns the aligment state currently set for the specified font.
;
; Params:
; font_h - Handle of font to be tested.
;
; Returns:
; 'True' if the font is currently set to include kerning when aligning.
; 'False' if the font is currently set to ignore kerning when aligning.
;
Function jf_get_kern_align(font_h%)

	f.jf_fontT = Object.jf_fontT(font_h)

	Return f\kern_align

End Function


;
; Specifies whether or not to ignore the shadow when spacing
; characters displayed using the given font.
;
; Params:
; font_h - Handle of font to be affected.
; state  - 'True' to ignore the shadow when spacing.
;          'False' to include the shadow when spacing.
;
Function jf_set_ignore_shadow(font_h%, state%)

	f.jf_fontT = Object.jf_fontT(font_h)

	state = (state <> False)

	shad_width = Abs(f\shad_dx)
	
	f\ignore_shad		= state
	f\ignore_shad_w	= (state = False) * shad_width
	f\shad_adjust		= (state = True) * shad_width
	
End Function


;
; Returns the shadow state currently set for the specified font.
;
; Params:
; font_h - Handle of font to be tested.
;
; Returns:
; 'True' if the font is currently set to ignore the shadow when spacing.
; 'False' if the font is currently set to include the shadow when spacing.
;
Function jf_get_ignore_shadow(font_h%)

	f.jf_fontT = Object.jf_fontT(font_h)

	Return f\ignore_shad
	
End Function


;
; Specifies an additional number of pixel rows to use when spacing apart
; word wrapped lines of text, displayed using the given font.
;
; Params:
; font_h  - Handle of font to be affected.
; spacing - Number of pixel rows to use when spacing. This is in addition
;           to the default spacing (i.e. the specified font's height). As such,
;           negative values can be used to squash lines together, but only up
;           to a maximum value of the font's height.
;
Function jf_set_line_spacing(font_h%, spacing%)

	f.jf_fontT = Object.jf_fontT(font_h)

	If spacing < -f\frame_height Then spacing = -f\frame_height
	
	f\line_spacing = spacing

End Function


;
; Returns the additional line spacing currently set for the specified font.
;
; Params:
; font_h - Handle of font to be tested.
;
; Returns:
; The amount of pixel rows currently used for line spacing.
; This value is in addition to the default spacing (i.e. the specified font's height).
;
Function jf_get_line_spacing(font_h%)

	f.jf_fontT = Object.jf_fontT(font_h)

	Return f\line_spacing

End Function


;
; Returns the height of the specified font.
;
; Params:
; font_h - Handle of font to be tested.
;
; Returns:
; The pixel height of the font (i.e. the height of all char frames).
;
Function jf_font_height(font_h%)

	f.jf_fontT = Object.jf_fontT(font_h)

	Return f\frame_height

End Function


;
; Returns the width of the specified font.
;
; Params:
; font_h - Handle of font to be tested.
;
; Returns:
; The pixel width of the font (i.e. the widest character frame).
;
Function jf_font_width(font_h%)

	f.jf_fontT = Object.jf_fontT(font_h)

	Return f\frame_widest

End Function


;
; Loads the specified font image and associated metrics file.
;
; Params:
; file$ - File spec of font image file to be loaded. The associated metrics
;         file (.jfm) must also reside in the same directory as the font image.
;
; Returns:
; The handle of the loaded font.
;
Function jf_load_font(file$)

	this_font.jf_fontT = New jf_fontT
	font_h = Handle(this_font)
	
	;
	; Load font image.
	;

	this_font\font_image = LoadImage(file$)

	If this_font\font_image = 0
		RuntimeError file$ + Chr$(13) + "Could not load font image."
	EndIf


	;
	; Raed associated Juicy Font Metrics (.jfm) file.
	;

	file$ = file$ + ".jfm"
	in_file = ReadFile(file$)

	If in_file = 0
		RuntimeError file$ + Chr$(13) + "Could not load font metrics."
	EndIf

	; Skip values used by the Juicy Fonts editor but not needed here.
	ReadString(in_file)
	ReadByte(in_file)
	ReadByte(in_file)
	ReadByte(in_file)
	
	; Read "ignore shadow" status.
	this_font\ignore_shad = ReadByte(in_file)

	; Read font image mask RGB.
	this_font\mask_r = ReadByte(in_file)
	this_font\mask_g = ReadByte(in_file)
	this_font\mask_b = ReadByte(in_file)

	; Read font shadow delta x.
	this_font\shad_dx = ReadInt(in_file)
	
	; Set default "ignore shadow" state for this font.
	jf_set_ignore_shadow(font_h,this_font\ignore_shad)
	
	; Read font frame height.
	this_font\frame_height = ReadShort(in_file)

	; Read "is fixed width?" flag to set default kern alignment.
	this_font\kern_align = (ReadByte(in_file) <> False)

	; Read widest character frame width.
	this_font\frame_widest = ReadShort(in_file)
	
	; Read each character's metrics.
	Repeat
		c = ReadByte(in_file)
		
		m.jf_metricsT = New jf_metricsT
		this_font\char_metrics[c] = m
		
		m\frame_width	= ReadShort(in_file)
		m\frame_x			= ReadShort(in_file)
		m\frame_y			= ReadShort(in_file)
		m\body_width	= ReadShort(in_file)
		m\kern_l			= ReadInt(in_file)
		m\kern_r			= ReadInt(in_file)
	Until Eof(in_file)

	CloseFile in_file

	MaskImage this_font\font_image,this_font\mask_r,this_font\mask_g,this_font\mask_b
	
	Return font_h

End Function


;
; Removes the specified font from memory so that it can no longer be used.
;
; Params:
; font_h - Handle of font to be freed.
;
Function jf_free_font(font_h%)

	f.jf_fontT = Object.jf_fontT(font_h)

	FreeImage f\font_image

	; Free all font character metrics.
	For c = 32 To 255
		If f\char_metrics[c] <> Null
			Delete f\char_metrics[c]
		EndIf
	Next
	
	Delete f
	
End Function


;
; Draws specified text on-screen using the specified font.
;
; Params:
; font_h     - Handle of font to be used.
; x,y        - Screen coords to align the text to.
; txt$       - The text to be displayed.
; align_x    - (Optional) Specifies how the text should be horizontally aligned:
;              0 - Left justify text. (Default)
;              1 - Centre text.
;              2 - Right justify text.
; align_y    - (Optional) Specifies how the text should be vertically aligned:
;              0 - Align to top of text. (Default)
;              1 - Align to centre of text.
;              2 - Align to bottom of text.
; wrap_width - (Optional) Specifies width to word wrap the text to.
;
Function jf_text(font_h%, x%, y%, txt$, align_x%=0, align_y%=0, wrap_width%=$7FFFFFFF)

	f.jf_fontT = Object.jf_fontT(font_h)

	; Remove any non-font (unprintable) characters from the text string.
	txt$ = jf_font_string(font_h,txt$)
	If txt$ = "" Then Return

	If f\kern_align
		SYS_create_lines_kern(f,x,y,txt$,align_x,align_y,wrap_width)
	Else
		SYS_create_lines_nokern(f,x,y,txt$,align_x,align_y,wrap_width)
	EndIf
	
	font_image		= f\font_image
	frame_height	= f\frame_height
	ignore_shad_w = f\ignore_shad_w
	
	;
	; Draw all the character image frames...
	;

	If f\shad_dx < 0
		For t.jf_lineT = Each jf_lineT
			; Draw char images from right-to-left.
			txt$ = t\txt$
			x = t\x
			y = t\y
			For n = Len(txt$) To 1 Step -1
				m.jf_metricsT = f\char_metrics[Asc(Mid$(txt$,n,1))]
				x = x - m\body_width - ignore_shad_w - m\kern_r
				DrawImageRect font_image,x,y, m\frame_x,m\frame_y,m\frame_width,frame_height
				x = x - m\kern_l
			Next
		Next
	Else
		For t.jf_lineT = Each jf_lineT
			; Draw char images from left-to-right.
			txt$ = t\txt$
			x = t\x
			y = t\y
			For n = 1 To Len(txt$)
				m.jf_metricsT = f\char_metrics[Asc(Mid$(txt$,n,1))]
				x = x + m\kern_l
				DrawImageRect font_image,x,y, m\frame_x,m\frame_y,m\frame_width,frame_height
				x = x + m\body_width + ignore_shad_w + m\kern_r
			Next
		Next
	EndIf
	
	; Delete all temp text lines.
	Delete Each jf_lineT

End Function


;
; Creates a fast, static text object, using the specified text and font.
;
; Params:
; font_h     - Handle of font to be used.
; x,y        - Screen coords to align the text to.
; txt$       - The text to be displayed.
; align_x    - (Optional) Specifies how the text should be horizontally aligned:
;              0 - Left justify text. (Default)
;              1 - Centre text.
;              2 - Right justify text.
; align_y    - (Optional) Specifies how the text should be vertically aligned:
;              0 - Align to top of text. (Default)
;              1 - Align to centre of text.
;              2 - Align to bottom of text.
; wrap_width - (Optional) Specifies width to word wrap the text to.
;
; Returns:
; The handle of the created text object.
;
Function jf_create_text(font_h%, x%, y%, txt$, align_x%=0, align_y%=0, wrap_width%=$7FFFFFFF)

	f.jf_fontT = Object.jf_fontT(font_h)

	; Remove any non-font (unprintable) characters from the text string.
	txt$ = jf_font_string(font_h,txt$)
	If txt$ = "" Then Return

	If f\kern_align
		SYS_create_lines_kern(f,x,y,txt$,align_x,align_y,wrap_width)
	Else
		SYS_create_lines_nokern(f,x,y,txt$,align_x,align_y,wrap_width)
	EndIf
	
	font_image		= f\font_image
	frame_height	= f\frame_height
	ignore_shad_w = f\ignore_shad_w
	
	char_bank	= CreateBank()
	offset		= 0
	
	;
	; Add all character metrics to bank...
	;

	If f\shad_dx < 0
		For t.jf_lineT = Each jf_lineT
			; Add characters from right-to-left.
			txt$ = t\txt$
			x = t\x
			y = t\y
			line_len = Len(txt$)

			ResizeBank char_bank,BankSize(char_bank) + (line_len * (5 * 4)) 

			For n = line_len To 1 Step -1
				m.jf_metricsT = f\char_metrics[Asc(Mid$(txt$,n,1))]
				x = x - m\body_width - ignore_shad_w - m\kern_r
				PokeInt char_bank,offset,x							: offset = offset + 4
				PokeInt char_bank,offset,y							: offset = offset + 4
				PokeInt char_bank,offset,m\frame_x			: offset = offset + 4
				PokeInt char_bank,offset,m\frame_y			: offset = offset + 4
				PokeInt char_bank,offset,m\frame_width	: offset = offset + 4
				x = x - m\kern_l
			Next
		Next
	Else
		For t.jf_lineT = Each jf_lineT
			; Add characters from left-to-right.
			txt$ = t\txt$
			x = t\x
			y = t\y
			line_len = Len(txt$)

			ResizeBank char_bank,BankSize(char_bank) + (line_len * (5 * 4)) 

			For n = 1 To line_len
				m.jf_metricsT = f\char_metrics[Asc(Mid$(txt$,n,1))]
				x = x + m\kern_l
				PokeInt char_bank,offset,x							: offset = offset + 4
				PokeInt char_bank,offset,y							: offset = offset + 4
				PokeInt char_bank,offset,m\frame_x			: offset = offset + 4
				PokeInt char_bank,offset,m\frame_y			: offset = offset + 4
				PokeInt char_bank,offset,m\frame_width	: offset = offset + 4
				x = x + m\body_width + ignore_shad_w + m\kern_r
			Next
		Next
	EndIf
	
	; Delete all temp text lines.
	Delete Each jf_lineT

	this_text.jf_textT	= New jf_textT
	this_text\font			= f
	this_text\char_bank	= char_bank

	Return Handle(this_text)

End Function


;
; Draws the text of a previously created text object.
;
; Params:
; text_h - Handle of text object to draw.
;
Function jf_draw_text(text_h%)
	
	t.jf_textT = Object.jf_textT(text_h)

	font_image		= t\font\font_image
	frame_height	= t\font\frame_height
	char_bank 		= t\char_bank
	
	;
	; Draw all bank character image frames...
	;

	For offset = 0 To BankSize(char_bank)-1 Step (5 * 4)
		x						= PeekInt(char_bank,offset)
		y						= PeekInt(char_bank,offset+4)
		frame_x			= PeekInt(char_bank,offset+8)
		frame_y			= PeekInt(char_bank,offset+12)
		frame_width	= PeekInt(char_bank,offset+16)

		DrawImageRect font_image,x,y, frame_x,frame_y,frame_width,frame_height
	Next
	
End Function


;
; Removes the specified text object from memory so that it can no longer be used.
;
; Params:
; text_h - Handle of text object to free.
;
Function jf_free_text(text_h%)

	t.jf_textT = Object.jf_textT(text_h)
	
	FreeBank t\char_bank
	Delete t
	
End Function


;
; Calculates the width, height and number of lines that would be used if
; the given text was displayed using the given font.
;
; Params:
; font_h     - Handle of font to be used.
; txt$       - The text to be measured.
; wrap_width - (Optional) Specifies width to word wrap the text to.
;
; Returns:
; Measurements are returned via the global variables jf_text_width,
; jf_text_height and jf_text_lines.
;
Function jf_measure_text(font_h%, txt$, wrap_width%=$7FFFFFFF)

	f.jf_fontT = Object.jf_fontT(font_h)

	; Remove any non-font (unprintable) characters from the text string.
	txt$ = jf_font_string(font_h,txt$)
	If txt$ = ""
		jf_text_width		= 0
		jf_text_height	= 0
		jf_text_lines		= 0
		Return
	EndIf	

	If f\kern_align
		SYS_text_size_kern(f,txt$,wrap_width)
	Else
		SYS_text_size_nokern(f,txt$,wrap_width)
	EndIf

End Function


;
; Removes all characters from a string that aren't present in the given font.
;
; Params:
; font_h - Handle of font to check the string against.
; s$     - The string to be converted.
;
; Returns:
; The converted string containing valid font characters only.
;
Function jf_font_string$(font_h%, s$)

	f.jf_fontT = Object.jf_fontT(font_h)

	For n = 1 To Len(s$)
		char$ = Mid$(s$,n,1)

		If f\char_metrics[Asc(char$)] <> Null
			out$ = out$ + char$
		EndIf
	Next
	
	Return out$
	
End Function




; ------------------------------------
; --- Juicy Fonts system functions ---
; ------------------------------------

;
; Creates system text line(s) (jf_lineT objects) representing the given
; text displayed using the given font, using kerning alignment.
;
; Params:
; f          - The font to to be used.
; x,y        - Screen coords to align the text to.
; txt$       - The text to be converted into system lines.
; align_x    - (Optional) Specifies how the text should be horizontally aligned:
;              0 - Left justify text. (Default)
;              1 - Centre text.
;              2 - Right justify text.
; align_y    - (Optional) Specifies how the text should be vertically aligned:
;              0 - Align to top of text. (Default)
;              1 - Align to centre of text.
;              2 - Align to bottom of text.
; wrap_width - (Optional) Specifies width to word wrap the text to.
;
Function SYS_create_lines_kern(f.jf_fontT, x%, y%, txt$, align_x%=0, align_y%=0, wrap_width%=$7FFFFFFF)

	ignore_shad_w	= f\ignore_shad_w
	shad_adjust		= f\shad_adjust
	wrap_width		= wrap_width - shad_adjust

	pos = 1
	last_char = Len(txt$)
	
	; Cut input string into separate, word wrapped lines of text.
	While pos <= last_char
		
		c = Asc(Mid$(txt$,pos,1))
		m.jf_metricsT = f\char_metrics[c]

		If Not line_width
			; Initialize a new text line.
			out_line.jf_lineT = New jf_lineT
			reach_l = 0
			reach_r = 0
			frame_x = 0
			space_pos = 0
			build$ = ""
			num_lines = num_lines + 1
		EndIf

		; Due to negative kerning, it's possible for a character to
		; reach further left onscreen than the char that precedes it.
		; We need to keep track of this via reach_l.
		frame_x = frame_x + m\kern_l
		If frame_x < 0
			If -frame_x > reach_l Then reach_l = -frame_x
		EndIf
		
		char_width = m\body_width + ignore_shad_w
		
		If m\kern_r < 0
			curr_width = reach_l + frame_x + char_width
		Else
			curr_width = reach_l + frame_x + char_width + m\kern_r
		EndIf
		
		If curr_width <= wrap_width
			; This character fits the current line...
			
			If c = 32
				; Record metrics of this most recent SPACE character.
				space_pos				= pos
				wrapped_width		= line_width
				wrapped_reach_r	= reach_r
				wrapped_chars		= Len(build$)
			EndIf
			
			If curr_width > line_width
				line_width = curr_width
				reach_r = 0
			Else
				reach_r = line_width - curr_width
			EndIf

			If m\kern_r < 0 Then reach_r = reach_r - m\kern_r ; + Abs(m\kern_r)

			frame_x = frame_x + char_width + m\kern_r
			build$ = build$ + Chr$(c)
			pos = pos + 1
		Else
			; This character is too wide for the current line...
			
			If c = 32
				; Skip/ignore this end-of-line space character.
				space_pos = 0
				pos = pos + 1
			EndIf
			
			If space_pos
				; Word wrap from rightmost space character.
				If wrapped_chars
					out_line\txt$			= Left$(build$,wrapped_chars)
					out_line\width		= wrapped_width + shad_adjust
					out_line\reach_l	= reach_l
					out_line\reach_r	= wrapped_reach_r
				Else
					; Word wrap from single space at start of line.
					space_m.jf_metricsT	= f\char_metrics[32]
					out_line\txt$		= " "
					out_line\width	= space_m\frame_width
					If space_m\kern_l < 0
						out_line\reach_l = -space_m\kern_l
					Else
						out_line\width = out_line\width + space_m\kern_l
					EndIf
					If space_m\kern_r < 0
						out_line\reach_r = -space_m\kern_r
					Else
						out_line\width = out_line\width + space_m\kern_r
					EndIf
				EndIf
				pos = space_pos + 1
			Else
				; Don't/can't word wrap.
				
				If build$ <> ""
					; Fill line with all chars excluding current one.
					out_line\txt$			= build$
					out_line\width		= line_width + shad_adjust
					out_line\reach_l	= reach_l
					out_line\reach_r	= reach_r
				Else
					; This char on it's own is too wide to fit inside an entire line!
					out_line\txt$			= Chr$(c)
					out_line\width		= curr_width + shad_adjust
					out_line\reach_l	= reach_l
					If m\kern_r < 0
						out_line\reach_r = -m\kern_r
					Else
						out_line\reach_r = 0
					EndIf
					If (c <> 32) Then pos = pos + 1
				EndIf
			EndIf
			
			line_width = 0
		EndIf
	Wend

	If line_width
		; Complete the final text line.
		out_line\txt$			= build$
		out_line\width		= line_width + shad_adjust
		out_line\reach_l	= reach_l
		out_line\reach_r	= reach_r
	EndIf

	line_height = f\frame_height + f\line_spacing

	; Set Y coord for first text line.
	Select align_y
	Case 1
		; Align to centre.
		y = y - (((line_height * num_lines) - f\line_spacing) / 2)
	Case 2
		; Align to bottom.
		y = y - (((line_height * num_lines) - f\line_spacing) - 1)
	End Select

	; Shadow orientation dictates whether the characters in each line will
	; need to be displayed from left-to-right or right-to-left.
	; 
	If f\shad_dx < 0
		; Set the x,y position of the last (rightmost) character in each
		; text line, according to the current alignment setting.
		Select align_x
		Case 1
			; Center justify.
			For t.jf_lineT = Each jf_lineT
				t\x = (x + (t\width / 2) - shad_adjust) - t\reach_r
				t\y = y
				y = y + line_height
			Next
		Case 2
			; Right justify.
			For t.jf_lineT = Each jf_lineT
				t\x = (x - shad_adjust + 1) - t\reach_r
				t\y = y
				y = y + line_height
			Next
		Default
			; Left justify.
			For t.jf_lineT = Each jf_lineT
				t\x = (x + t\width - shad_adjust) - t\reach_r
				t\y = y
				y = y + line_height
			Next
		End Select
	Else
		; Set the x,y position of the first (leftmost) character in each
		; text line, according to the current alignment setting.
		Select align_x
		Case 1
			; Center justify.
			For t.jf_lineT = Each jf_lineT
				t\x = (x - (t\width / 2)) + t\reach_l
				t\y = y
				y = y + line_height
			Next
		Case 2
			; Right justify.
			For t.jf_lineT = Each jf_lineT
				t\x = (x - t\width + 1) + t\reach_l
				t\y = y
				y = y + line_height
			Next
		Default
			; Left justify.
			For t.jf_lineT = Each jf_lineT
				t\x = x + t\reach_l
				t\y = y
				y = y + line_height
			Next
		End Select
	EndIf

End Function


;
; Creates system text line(s) (jf_lineT objects) representing the given
; text displayed using the given font, using kernless alignment.
;
; Params:
; f          - The font to to be used.
; x,y        - Screen coords to align the text to.
; txt$       - The text to be converted into system lines.
; align_x    - (Optional) Specifies how the text should be horizontally aligned:
;              0 - Left justify text. (Default)
;              1 - Centre text.
;              2 - Right justify text.
; align_y    - (Optional) Specifies how the text should be vertically aligned:
;              0 - Align to top of text. (Default)
;              1 - Align to centre of text.
;              2 - Align to bottom of text.
; wrap_width - (Optional) Specifies width to word wrap the text to.
;
Function SYS_create_lines_nokern(f.jf_fontT, x%, y%, txt$, align_x%=0, align_y%=0, wrap_width%=$7FFFFFFF)

	ignore_shad_w	= f\ignore_shad_w
	shad_adjust		= f\shad_adjust
	wrap_width		= wrap_width - shad_adjust

	pos = 1
	last_char = Len(txt$)
	
	; Cut input string into separate, word wrapped lines of text.
	While pos <= last_char
		
		c = Asc(Mid$(txt$,pos,1))
		m.jf_metricsT = f\char_metrics[c]

		If Not line_width
			; Initialize a new text line.
			out_line.jf_lineT = New jf_lineT
			reach_l		= 0
			reach_r		= 0
			frame_x		= 0
			space_pos	= 0
			build$		= ""
			num_lines	= num_lines + 1
		Else
			; We only include the left kern if this character
			; is NOT the first (leftmost) in this line.
			frame_x = frame_x + m\kern_l
			
			; Due to negative kerning, it's possible for a character to
			; reach further left onscreen than the char that precedes it.
			; We need to keep track of this via reach_l.
			If frame_x < 0
				If -frame_x > reach_l Then reach_l = -frame_x
			EndIf
		EndIf

		char_width = m\body_width + ignore_shad_w
		curr_width = reach_l + frame_x + char_width

		If curr_width <= wrap_width
			; This character fits the current line...
			
			If c = 32
				; Record metrics of this most recent SPACE character.
				space_pos				= pos
				wrapped_width		= line_width
				wrapped_reach_r	= reach_r
				wrapped_chars		= Len(build$)
			EndIf
			
			If curr_width > line_width
				line_width = curr_width
				reach_r = 0
			Else
				reach_r = line_width - curr_width
			EndIf

			frame_x = frame_x + char_width + m\kern_r
			build$ = build$ + Chr$(c)
			pos = pos + 1
		Else
			; This character is too wide for the current line...

			If c = 32
				; Skip/ignore this end-of-line space character.
				space_pos = 0
				pos = pos + 1
			EndIf
			
			If space_pos
				; Word wrap from rightmost space character.
				If wrapped_chars
					out_line\txt$			= Left$(build$,wrapped_chars)
					out_line\width		= wrapped_width + shad_adjust
					out_line\reach_l	= reach_l
					out_line\reach_r	= wrapped_reach_r
				Else
					; Word wrap from single space at start of line.
					out_line\txt$			= " "
					out_line\width		= f\char_metrics[32]\frame_width
					out_line\reach_l	= 0
					out_line\reach_r	= 0
				EndIf
				pos = space_pos + 1
			Else
				; Don't/can't word wrap.
				
				If build$ <> ""
					; Fill line with all chars excluding current one.
					out_line\txt$			= build$
					out_line\width		= line_width + shad_adjust
					out_line\reach_l	= reach_l
					out_line\reach_r	= reach_r
				Else
					; This char on it's own is too wide to fit inside an entire line!
					out_line\txt$			= Chr$(c)
					out_line\width		= char_width + shad_adjust
					out_line\reach_l	= 0
					out_line\reach_r	= 0
					If (c <> 32) Then pos = pos + 1
				EndIf
			EndIf
			
			line_width = 0
		EndIf
	Wend

	If line_width
		; Complete the final text line.
		out_line\txt$			= build$
		out_line\width		= line_width + shad_adjust
		out_line\reach_l	= reach_l
		out_line\reach_r	= reach_r
	EndIf

	line_height = f\frame_height + f\line_spacing
	
	; Set Y coord for first text line.
	Select align_y
	Case 1
		; Align to centre.
		y = y - (((line_height * num_lines) - f\line_spacing) / 2)
	Case 2
		; Align to bottom.
		y = y - (((line_height * num_lines) - f\line_spacing) - 1)
	End Select
	
	; Shadow orientation dictates whether the characters in each line will
	; need to be displayed from left-to-right or right-to-left.
	; 
	If f\shad_dx < 0
		; Set the x,y position of the last (rightmost) character in each
		; text line, according to the current alignment setting.
		Select align_x
		Case 1
			; Centre justify.
			For t.jf_lineT = Each jf_lineT
				last_kern = f\char_metrics[Asc(Right$(t\txt$,1))]\kern_r
				t\x = (x + (t\width / 2) - shad_adjust) - t\reach_r + last_kern
				t\y = y
				y = y + line_height
			Next
		Case 2
			; Right justify.
			For t.jf_lineT = Each jf_lineT
				last_kern = f\char_metrics[Asc(Right$(t\txt$,1))]\kern_r
				t\x = (x - shad_adjust + 1) - t\reach_r + last_kern
				t\y = y
				y = y + line_height
			Next
		Default
			; Left justify.
			For t.jf_lineT = Each jf_lineT
				last_kern = f\char_metrics[Asc(Right$(t\txt$,1))]\kern_r
				t\x = (x + t\width - shad_adjust) - t\reach_r + last_kern
				t\y = y
				y = y + line_height
			Next
		End Select
	Else
		; Set the x,y position of the first (leftmost) character in each
		; text line, according to the current alignment setting.
		Select align_x
		Case 1
			; Centre justify.
			For t.jf_lineT = Each jf_lineT
				first_kern = f\char_metrics[Asc(Left$(t\txt$,1))]\kern_l
				t\x = (x - (t\width / 2)) + t\reach_l - first_kern
				t\y = y
				y = y + line_height
			Next
		Case 2
			; Right justify.
			For t.jf_lineT = Each jf_lineT
				first_kern = f\char_metrics[Asc(Left$(t\txt$,1))]\kern_l
				t\x = (x - t\width + 1) + t\reach_l - first_kern
				t\y = y
				y = y + line_height
			Next
		Default
			; Left justify.
			For t.jf_lineT = Each jf_lineT
				first_kern = f\char_metrics[Asc(Left$(t\txt$,1))]\kern_l
				t\x = x + t\reach_l - first_kern
				t\y = y
				y = y + line_height
			Next
		End Select
	EndIf

End Function


;
; Calculates the width, height and number of lines that would be used if
; the given text was displayed using the given font, using kerning alignment.
;
; Params:
; f          - The font to be used.
; txt$       - The text to be measured.
; wrap_width - (Optional) Specifies width to word wrap the text to.
;
; Returns:
; Measurements are returned via the global variables jf_text_width,
; jf_text_height and jf_text_lines.
;
Function SYS_text_size_kern(f.jf_fontT, txt$, wrap_width%=$7FFFFFFF)

	ignore_shad_w	= f\ignore_shad_w
	shad_adjust		= f\shad_adjust
	wrap_width		= wrap_width - shad_adjust

	pos = 1
	last_char = Len(txt$)
	
	; Cut input string into separate, word wrapped lines of text.
	While pos <= last_char
		
		c = Asc(Mid$(txt$,pos,1))
		m.jf_metricsT = f\char_metrics[c]

		If Not line_width
			; Initialize a new text line.
			reach_l			= 0
			frame_x			= 0
			space_pos		= 0
			line_empty	= True
			num_lines		= num_lines + 1
		EndIf

		frame_x = frame_x + m\kern_l

		; Due to negative kerning, it's possible for a character to
		; reach further left onscreen than the char that precedes it.
		; We need to keep track of this via reach_l.
		If frame_x < 0
			If -frame_x > reach_l Then reach_l = -frame_x
		EndIf
		
		char_width = m\body_width + ignore_shad_w
		
		If m\kern_r < 0
			curr_width = reach_l + frame_x + char_width
		Else
			curr_width = reach_l + frame_x + char_width + m\kern_r
		EndIf
		
		If curr_width <= wrap_width
			; This character fits the current line...

			If c = 32
				; Record metrics of this most recent SPACE character.
				space_pos			= pos
				wrapped_width	= line_width
				start_space		= line_empty
			EndIf
			
			If curr_width > line_width Then line_width = curr_width
			
			frame_x = frame_x + char_width + m\kern_r
			line_empty = False
			pos = pos + 1
		Else
			; This character is too wide for the current line...
			
			If c = 32
				; Skip/ignore this end-of-line space character.
				space_pos = 0
				pos = pos + 1
			EndIf
			
			If space_pos
				; Line contains a SPACE character...

				If start_space
					; Word wrap from single space at start of line.
					space_m.jf_metricsT = f\char_metrics[32]
					this_width = space_m\frame_width
					If space_m\kern_l > 0 Then this_width = this_width + space_m\kern_l
					If space_m\kern_r > 0 Then this_width = this_width + space_m\kern_r
				Else
					; Word wrap from rightmost space character.
					this_width = wrapped_width + shad_adjust
				EndIf

				pos = space_pos + 1
			Else
				; Don't/can't word wrap.
				
				If line_empty
					; This char on it's own is too wide to fit inside an entire line!
					this_width = curr_width + shad_adjust
					If (c <> 32) Then pos = pos + 1
				Else
					; Line is all chars EXCEPT current one.
					this_width = line_width + shad_adjust
				EndIf
			EndIf

			If this_width > widest_line Then widest_line = this_width
			
			; Signal to start a new line.
			line_width = 0
		EndIf
	Wend

	If line_width
		; Complete the final text line.
		this_width = line_width + shad_adjust
		If this_width > widest_line Then widest_line = this_width
	EndIf

	; Return text metrics via system globals.
	jf_text_width		= widest_line
	jf_text_height	= ((f\frame_height + f\line_spacing) * num_lines) - f\line_spacing
	jf_text_lines		= num_lines

End Function


;
; Calculates the width, height and number of lines that would be used if
; the given text was displayed using the given font, using kernless alignment.
;
; Params:
; f          - The font to be used.
; txt$       - The text to be measured.
; wrap_width - (Optional) Specifies width to word wrap the text to.
;
; Returns:
; Measurements are returned via the global variables jf_text_width,
; jf_text_height and jf_text_lines.
;
Function SYS_text_size_nokern(f.jf_fontT, txt$, wrap_width%=$7FFFFFFF)

	ignore_shad_w	= f\ignore_shad_w
	shad_adjust		= f\shad_adjust
	wrap_width		= wrap_width - shad_adjust

	pos = 1
	last_char = Len(txt$)
	
	; Cut input string into separate, word wrapped lines of text.
	While pos <= last_char
		
		c = Asc(Mid$(txt$,pos,1))
		m.jf_metricsT = f\char_metrics[c]

		If Not line_width
			; Initialize a new text line.
			reach_l			= 0
			frame_x			= 0
			space_pos		= 0
			line_empty	= True
			num_lines		= num_lines + 1
		Else
			; We only include the left kern if this character
			; is NOT the first (leftmost) in this line.
			frame_x = frame_x + m\kern_l
			
			; Due to negative kerning, it's possible for a character to
			; reach further left onscreen than the char that precedes it.
			; We need to keep track of this via reach_l.
			If frame_x < 0
				If -frame_x > reach_l Then reach_l = -frame_x
			EndIf
		EndIf

		char_width = m\body_width + ignore_shad_w
		curr_width = reach_l + frame_x + char_width

		If curr_width <= wrap_width
			; This character fits the current line...
			
			If c = 32
				; Record metrics of this most recent SPACE character.
				space_pos			= pos
				wrapped_width	= line_width
				start_space		= line_empty
			EndIf
			
			If curr_width > line_width Then line_width = curr_width

			frame_x = frame_x + char_width + m\kern_r
			line_empty = False
			pos = pos + 1
		Else
			; This character is too wide for the current line...

			If c = 32
				; Skip/ignore this end-of-line space character.
				space_pos = 0
				pos = pos + 1
			EndIf
			
			If space_pos
				; Line contains a SPACE character...

				If start_space
					; Word wrap from single space at start of line.
					this_width = f\char_metrics[32]\frame_width
				Else
					; Word wrap from rightmost space character.
					this_width = wrapped_width + shad_adjust
				EndIf

				pos = space_pos + 1
			Else
				; Don't/can't word wrap...
				
				If line_empty
					; This char on it's own is too wide to fit inside an entire line!
					this_width = char_width + shad_adjust
					If (c <> 32) Then pos = pos + 1
				Else
					; Line is all chars EXCEPT current one.
					this_width = line_width + shad_adjust
				EndIf
			EndIf

			If this_width > widest_line Then widest_line = this_width
			
			; Signal to start a new line.
			line_width = 0
		EndIf
	Wend

	If line_width
		; Complete the final text line.
		this_width = line_width + shad_adjust
		If this_width > widest_line Then widest_line = this_width
	EndIf

	; Return text metrics via system globals.
	jf_text_width		= widest_line
	jf_text_height	= ((f\frame_height + f\line_spacing) * num_lines) - f\line_spacing
	jf_text_lines		= num_lines
	
End Function