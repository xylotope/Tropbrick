;;;;;;;;;;;;;;;;;;;;;;;;;;
; Tropical Brick Breaker ;
; Shane Daniels          ;;;
; Thursday, March 30, 2006 ;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;ChangeDir SystemProperty$("appdir")
;;;;;;;;;;;;;;;;;;;;;;;;
; Setup Graphics Stuff ;
;;;;;;;;;;;;;;;;;;;;;;;;
Include "Lib\cubemap.bb"
file=OpenFile("settings.gfx")
Global width%=ReadLine(file)
Global mw#=width/2
Global height%=ReadLine(file)
Global mh#=height/2
Global windowed = 0
Global musicVolume = 50
Global sfxVolume = 50
Global ambientVolume = 50
Global parrotSound = 1
Global depth%=ReadLine(file)
Global mode%=ReadLine(file)
musicVolume = ReadLine(file)
sfxVolume = ReadLine(file)
ambientVolume = ReadLine(file)
parrotSound = ReadLine(file)
CloseFile(file)
file=0
If mode > 1
	windowed = 1
EndIf
Graphics3D width,height,depth,mode
If depth%=16
	Dither True
Else
	Dither False
EndIf
width%=0
height%=0
depth%=0
SetBuffer BackBuffer()
Global timer=CreateTimer(60)
Color(1,1,1)
SeedRnd(MilliSecs())
AppTitle("Tropbrick")
;;;;;;;;;;;;;;;;;;;;;;;;;
; End of Graphics Setup ;
;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;
;   Include Game File   ;
;;;;;;;;;;;;;;;;;;;;;;;;;
Include "game.bb"
;;;;;;;;
; Sine ;
;;;;;;;;
Global derizzle=0
Global numofframes%=36
Dim SineValue#(numofframes)
CalcSine()
;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Setup Cam and Light Stuff ;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
Global pivot=CreatePivot()
Global cam=CreateCamera(pivot)
Global light=CreateLight(2)
LightColor(light,255,255,201)
MoveEntity(light,500,500,0)
;Global light2=CreateLight(2)
MoveEntity(cam,0,50,-120)
TurnEntity(cam,20,0,0)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; End Setup Cam and Light Stuff ;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
Global l_surface
Global l_texture
;;;;;;;;;;;;;;;
; Cubemapping ;
;;;;;;;;;;;;;;;
;SetCubemapping(1,1000,0,500,1000,0,63,255)
;;;;;;;;;;;;;;;
; End Cubemap ;
;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;
;Load Menu Objects ;
;;;;;;;;;;;;;;;;;;;;
Type particlem
	Field entity
	Field time
	Field timer
	Field xspeed
	Field yspeed
	Field zspeed
	Field red
	Field green
	Field blue
	Field order
	Field scale
End Type
Global water,watersurf,watertex,waterref
Global mousesprite
Global skycube
Global particleentity
Global mousedot
Global scoresButton
Global overScores = 0
Global titlesprite,playsprite,optionssprite,creditssprite,exitsprite
Global tRot#,pRot,oRot,cRot,eRot
Global tDir#,pDir,oDir,cDir,eDir
Global optionsmenusprite
Global island
Global wtrdepth#
Global wtrvertx#,wtrverty#,wtrvertz#
Global ph#
;
Global cN1=LoadSprite("MenuObjects\Programmer.bmp",1+4)
HideEntity(cN1)
Global cN2=LoadSprite("MenuObjects\ShaneDaniels.bmp",1+4)
HideEntity(cN2)
Global cN3=LoadSprite("MenuObjects\Artist.bmp",1+4)
HideEntity(cN3)
Global cN4=LoadSprite("MenuObjects\JesseHagerman.bmp",1+4)
HideEntity(cN4)
Global cN5 = LoadSprite("MenuObjects\Music.bmp", 1+4)
HideEntity(cN5)
Global cN6 = LoadSprite("MenuObjects\Incompetech.bmp", 1+4)
HideEntity(cN6)
Global creditsName1
Global creditsName2
Global creditsName3
Global creditsName4
Global creditsName5
Global creditsName6
LoadMenu()
;;;;;;;;;;;;;;;;;;;;
; Music and Sounds ;
;;;;;;;;;;;;;;;;;;;;
;Global music=LoadSound()
;LoopSound music
;PlaySound music
Global playedSound = 0
;;;;;;;;;;;;;;;;;;;;;;;;
; End Music and Sounds ;
;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;
; End Load Menu Objects ;
;;;;;;;;;;;;;;;;;;;;;;;;;
Dim spheres(wtrvrtcnt)
;;;;;;;;;;;;;
; Main Loop ;
;;;;;;;;;;;;;
Repeat
Cls

	ph#=MilliSecs()/10.3       ;Frequency of waves (time)
	wtrvrtcnt%=CountVertices(watersurf)-1
		
	For k=0 To wtrvrtcnt Step 3            ;Loop through mesh and update vertices
		wtrvertx#=VertexX(watersurf,k)
		wtrvertz#=VertexZ(watersurf,k)
		wtrverty#=Sin(ph+wtrvertz*200)*wtrdepth ;Create wave
		VertexCoords watersurf,k,wtrvertx,wtrverty,wtrvertz ;Update
	Next
	;watupdate=watupdate+1
	;If watupdate>0
		UpdateNormals(water)
	;	watupdate=0
	;EndIf
mx#=MouseX()-mw
my#=-MouseY()+mh
PositionEntity(mousesprite,mx,my,mw);mx*.0095,my*.0095,5)

PositionEntity(skycube,EntityX(cam,True),EntityY(cam,True),EntityZ(cam,True))
TurnEntity(pivot,0,.1,0)
;
parti.particlem=New particlem
parti\entity=CopyEntity(particleentity,mousesprite)
parti\time=0
parti\timer=18
parti\xspeed=0;Rand(-1,1)
parti\yspeed=5
parti\zspeed=1;Rand(-1,1)
parti\red=255
parti\green=255
parti\blue=0
parti\order=-110
parti\scale=20
EntityParent(parti\entity,0)
TurnEntity(parti\entity,0,Rand(0,360),0)
For parti.particlem=Each particlem
	EntityColor(parti\entity,parti\red,parti\green,parti\blue)
	;EntityOrder(parti\entity,parti\order)
	MoveEntity(parti\entity,0,0,1)
	ScaleSprite(parti\entity,parti\scale,parti\scale)
	TranslateEntity(parti\entity,0,parti\yspeed,0);parti\xspeed,parti\yspeed,parti\zspeed)
	TurnEntity(parti\entity,0,1,0)
	parti\red=parti\red-20
	;parti\green=parti\green+1
	;parti\blue=parti\blue+1
	;parti\order=parti\order+1
	parti\scale=parti\scale-1
	;parti\yspeed=parti\yspeed-2
	parti\time=parti\time+1
	If parti\time>parti\timer
		FreeEntity(parti\entity)
		Delete parti
	EndIf
Next
mousx%=MouseX()
mousy%=MouseY()
If ImageRectOverlap(mousedot,mousx,mousy, 451, 386, 137, 87)
	If playedSound <> 1
		PlaySound(soundPick1)
		playedSound = 1
	EndIf
	EntityColor(playsprite,200,255,0)
	EntityColor(optionssprite,255,255,255)
	EntityColor(creditssprite,255,255,255)
	EntityColor(exitsprite,255,255,255)
	For number=1 To 1
		parti.particlem=New particlem
		parti\entity=CopyEntity(particleentity,mousesprite)
		parti\time=0
		parti\timer=18
		parti\xspeed=0;Rand(-1,1)
		parti\yspeed=5
		parti\zspeed=1;Rand(-1,1)
		parti\red=255
		parti\green=255
		parti\blue=0
		parti\order=-110
		parti\scale=20
		EntityParent(parti\entity,0)
		TurnEntity(parti\entity,0,Rand(0,360),0)
	Next
	If MouseHit(1)
		optionselected=1
	Else
		optionselected=0
	EndIf
ElseIf ImageRectOverlap(mousedot,mousx,mousy, 410, 483, 206, 80)
	If playedSound <> 2
		PlaySound(soundPick1)
		playedSound = 2
	EndIf
	EntityColor(playsprite,255,255,255)
	EntityColor(optionssprite,200,255,0)
	EntityColor(creditssprite,255,255,255)
	EntityColor(exitsprite,255,255,255)
	For number=1 To 1
		parti.particlem=New particlem
		parti\entity=CopyEntity(particleentity,mousesprite)
		parti\time=0
		parti\timer=18
		parti\xspeed=0;Rand(-1,1)
		parti\yspeed=5
		parti\zspeed=1;Rand(-1,1)
		parti\red=255
		parti\green=255
		parti\blue=0
		parti\order=-110
		parti\scale=20
		EntityParent(parti\entity,0)
		TurnEntity(parti\entity,0,Rand(0,360),0)
	Next
	If MouseHit(1)
		optionselected=2
	Else
		optionselected=0
	EndIf
ElseIf ImageRectOverlap(mousedot,mousx,mousy, 410, 571, 205, 73)
	If playedSound <> 3
		PlaySound(soundPick1)
		playedSound = 3
	EndIf
	EntityColor(playsprite,255,255,255)
	EntityColor(optionssprite,255,255,255)
	EntityColor(creditssprite,200,255,0)
	EntityColor(exitsprite,255,255,255)
	For number=1 To 1
		parti.particlem=New particlem
		parti\entity=CopyEntity(particleentity,mousesprite)
		parti\time=0
		parti\timer=18
		parti\xspeed=0;Rand(-1,1)
		parti\yspeed=5
		parti\zspeed=1;Rand(-1,1)
		parti\red=255
		parti\green=255
		parti\blue=0
		parti\order=-110
		parti\scale=20
		EntityParent(parti\entity,0)
		TurnEntity(parti\entity,0,Rand(0,360),0)
	Next
	If MouseHit(1)
		optionselected=3
	Else
		optionselected=0
	EndIf
ElseIf ImageRectOverlap(mousedot,mousx,mousy, 452, 665, 128, 70)
	If playedSound <> 4
		PlaySound(soundPick1)
		playedSound = 4
	EndIf
	EntityColor(playsprite,255,255,255)
	EntityColor(optionssprite,255,255,255)
	EntityColor(creditssprite,255,255,255)
	EntityColor(exitsprite,200,255,0)
	For number=1 To 1
		parti.particlem=New particlem
		parti\entity=CopyEntity(particleentity,mousesprite)
		parti\time=0
		parti\timer=18
		parti\xspeed=0;Rand(-1,1)
		parti\yspeed=5
		parti\zspeed=1;Rand(-1,1)
		parti\red=255
		parti\green=255
		parti\blue=0
		parti\order=-110
		parti\scale=20
		EntityParent(parti\entity,0)
		TurnEntity(parti\entity,0,Rand(0,360),0)
	Next
	If MouseHit(1)
		optionselected=4
	Else
		optionselected=0
	EndIf
ElseIf ImageRectOverlap(mousedot, mousx, mousy, 700, 471, 101, 104)
	If playedSound <> 5
		PlaySound(soundPick1)
		playedSound = 5
	EndIf
	If overScores <> 1
		overScores = 1
		EntityColor(scoresButton, 200, 255, 0)
		Animate(scoresButton, 1, .1)
		;FlushKeys()
		;WaitKey()
	EndIf
	If MouseHit(1)
		HighScoreTable()
	EndIf
Else
	If overScores <> 0
		overScores = 0
		EntityColor(scoresButton, 255, 255, 255)
		Animate(scoresButton, 0)
	EndIf
	EntityColor(playsprite,255,255,255)
	EntityColor(optionssprite,255,255,255)
	EntityColor(creditssprite,255,255,255)
	EntityColor(exitsprite,255,255,255)
	optionselected=0
	playedSound = 0
EndIf
FlushMouse()
Select optionselected
	;;;;;;;;;;;;;
	; Game Loop ;
	;;;;;;;;;;;;;
	Case 1
		PlaySound(soundWater1)
		;Repeat
		;	Cls
		;	
		;	UpdateWorld()
		;	WaitTimer(timer)
		;	RenderWorld()
		;	Flip
		;Until KeyHit(1)
		;ExecFile("Game.exe")
		FlushMouse()
		FlushKeys()
		FlushJoy()
		HideEntity(titlesprite)
		HideEntity(playsprite)
		HideEntity(optionssprite)
		HideEntity(creditssprite)
		HideEntity(exitsprite)
		;;;;;;;;;;;;;;;;;;
		;Fail()
		;HighScoreTable()
		option=PlayMenu()
		;;;;;;;;;;;;;;;;;;
		ShowEntity(titlesprite)
		ShowEntity(playsprite)
		ShowEntity(optionssprite)
		ShowEntity(creditssprite)
		ShowEntity(exitsprite)
		FlushKeys()
		FlushJoy()
		FlushMouse()
		;;;;;;;;;;;;;;;;;;
		If option<>3
			CameraProjMode(cam,0)
			CameraProjMode(gameCam,1)
			FreeMenu()
			Game(option)
			LoadMenu()
			CameraProjMode(gameCam,0)
			CameraProjMode(cam,1)
			FlushKeys()
			FlushJoy()
			FlushMouse()
		EndIf
	;;;;;;;;;;;
	; Options ;
	;;;;;;;;;;;
	Case 2
		FlushMouse()
		FlushKeys()
		FlushJoy()
		HideEntity(titlesprite)
		HideEntity(playsprite)
		HideEntity(optionssprite)
		HideEntity(creditssprite)
		HideEntity(exitsprite)
		ShowEntity(optionsmenusprite)
		sliderSprite = LoadSprite("MenuObjects\slider.bmp", 1+4)
		sliderSprite2 = LoadSprite("MenuObjects\slider.bmp", 1+4)
		sliderSprite3 = LoadSprite("MenuObjects\slideer.bmp", 1+4)
		sliderSprite4 = LoadSprite("MenuObjects\slideer.bmp", 1+4)
		sliderSprite5 = LoadSprite("MenuObjects\slideer.bmp", 1+4)
		oSave = LoadSprite("MenuObjects\oSave.bmp", 1+4)
		oCancel = LoadSprite("MenuObjects\oCancel.bmp", 1+4)
		EntityParent(sliderSprite, cam)
		EntityParent(sliderSprite2, cam)
		EntityParent(sliderSprite3, cam)
		EntityParent(sliderSprite4, cam)
		EntityParent(sliderSprite5, cam)
		EntityParent(oSave, cam)
		EntityParent(oCancel, cam)
		EntityOrder(sliderSprite, -101)
		EntityOrder(sliderSprite2, -101)
		EntityOrder(sliderSprite3, -101)
		EntityOrder(sliderSprite4, -101)
		EntityOrder(sliderSprite5, -101)
		EntityOrder(oSave, -101)
		EntityOrder(oCancel, -101)
		HideEntity(oSave)
		HideEntity(oCancel)
		ScaleSprite(sliderSprite, .18, .09)
		ScaleSprite(sliderSprite2, .18, .09)
		ScaleSprite(sliderSprite3, .18, .09)
		ScaleSprite(sliderSprite4, .18, .09)
		ScaleSprite(sliderSprite5, .18, .09)
		ScaleSprite(oSave, .135, .043)
		ScaleSprite(oCancel, .135, .043)
		overSave = 0
		overCancel = 0
		If parrotSound = 1
			PositionEntity(sliderSprite, 1.382,  -.422, 5)
		Else
			PositionEntity(sliderSprite, .9,  -.422, 5)
		EndIf
		If windowed <> 1
			PositionEntity(sliderSprite2, 1.382, -1.143, 5)
		Else
			PositionEntity(sliderSprite2, .9, -1.143, 5)
		EndIf
		PositionEntity(oSave, -.324, -.4275, 1.01)
		PositionEntity(oCancel, .3165, -.427, 1.01)
		;3.59 * percent - .28
		slX# = musicVolume * .035105 - .28
		If slX < -.28
			slX = -.28
		ElseIf slX > 3.2305
			slX = 3.2305
		EndIf
		PositionEntity(sliderSprite3, slX, 1.49, 5)
		slX# = sfxVolume * .035105 - .28
		If slX < -.28
			slX = -.28
		ElseIf slX > 3.2305
			slX = 3.2305
		EndIf
		PositionEntity(sliderSprite4, slX, .87, 5)
		slX# = ambientVolume * .035105 - .28
		If slX < -.28
			slX = -.28
		ElseIf slX > 3.2305
			slX = 3.2305
		EndIf
		PositionEntity(sliderSprite5, slX, .17, 5)
		s3Min# = musicVolume * 3.61 - 50
		s3Max# = musicVolume * 3.61 - 42
		s4Min# = sfxVolume * 3.61 - 50
		s4Max# = sfxVolume * 3.61 - 42
		s5Min# = ambientVolume * 3.61 - 50
		s5Max# = ambientVolume * 3.61 - 42
		Repeat
			Cls
			;
			mx#=MouseX()-mw
			my#=-MouseY()+mh
			PositionEntity(mousesprite,mx,my,mw);mx*.0095,my*.0095,5)
			MoveEntity(water,.1,0,.1)
			PositionEntity(skycube,EntityX(cam,True),EntityY(cam,True),EntityZ(cam,True))
			TurnEntity(pivot,0,.1,0)
			;
			parti.particlem=New particlem
			parti\entity=CopyEntity(particleentity,mousesprite)
			parti\time=0
			parti\timer=18
			parti\xspeed=0;Rand(-1,1)
			parti\yspeed=5
			parti\zspeed=1;Rand(-1,1)
			parti\red=255
			parti\green=255
			parti\blue=0
			parti\order=-110
			parti\scale=20
			EntityParent(parti\entity,0)
			TurnEntity(parti\entity,0,Rand(0,360),0)
			For parti.particlem=Each particlem
				EntityColor(parti\entity,parti\red,parti\green,parti\blue)
				;EntityOrder(parti\entity,parti\order)
				MoveEntity(parti\entity,0,0,1)
				ScaleSprite(parti\entity,parti\scale,parti\scale)
				TranslateEntity(parti\entity,0,parti\yspeed,0);parti\xspeed,parti\yspeed,parti\zspeed)
				TurnEntity(parti\entity,0,1,0)
				parti\red=parti\red-20
				;parti\green=parti\green+1
				;parti\blue=parti\blue+1
				;parti\order=parti\order+1
				parti\scale=parti\scale-1
				;parti\yspeed=parti\yspeed-2
				parti\time=parti\time+1
				If parti\time>parti\timer
					FreeEntity(parti\entity)
					Delete parti
				EndIf
			Next
			
			ph#=MilliSecs()/10.3       ;Frequency of waves (time)
			wtrvrtcnt%=CountVertices(watersurf)-1
				
			For k=0 To wtrvrtcnt Step 3            ;Loop through mesh and update vertices
				wtrvertx#=VertexX(watersurf,k)
				wtrvertz#=VertexZ(watersurf,k)
				wtrverty#=Sin(ph+wtrvertz*200)*wtrdepth ;Create wave
				VertexCoords watersurf,k,wtrvertx,wtrverty,wtrvertz ;Update
			Next
			
			If mx > -230 And my < -198 And mx < -94 And my > -233
				If overSave <> 1
					overSave = 1
					ShowEntity(oSave)
				EndIf
			ElseIf mx > 90 And my < -198 And mx < 228 And my > -233
				If overCancel <> 1
					overCancel = 1
					ShowEntity(oCancel)
				EndIf
			Else
				If overSave <> 0
					overSave = 0
					HideEntity(oSave)
				EndIf
				If overCancel <> 0
					overCancel = 0
					HideEntity(oCancel)
				EndIf
			EndIf
			If Not MouseDown(1)
				sl3Down = False
				sl4Down = False
				sl5Down = False
			EndIf
			If sl3Down
				musicVolume = ((mx + 46.0) / 3.61)
				If musicVolume < 0
					musicVolume = 0
				ElseIf musicVolume > 100
					musicVolume = 100
				EndIf
				slX# = mx / 102.83435 + 0.16732133;((mx + 46.0) / 3.61) * .035105 - .28
				If slX < -.28
					slX = -.28
				ElseIf slX > 3.2305
					slX = 3.2305
				Else
					s3Min = mx - 4
					s3Max = mx + 4
				EndIf
				PositionEntity(sliderSprite3, slX, 1.49, 5)
			ElseIf sl4Down
				sfxVolume = ((mx + 46.0) / 3.61)
				If sfxVolume < 0
					sfxVolume = 0
				ElseIf sfxVolume > 100
					sfxVolume = 100
				EndIf
				slX# = mx / 102.83435 + 0.16732133;((mx + 46.0) / 3.61) * .035105 - .28
				If slX < -.28
					slX = -.28
				ElseIf slX > 3.2305
					slX = 3.2305
				Else
					s4Min = mx - 4
					s4Max = mx + 4
				EndIf
				PositionEntity(sliderSprite4, slX, .87, 5)
			ElseIf sl5Down
				ambientVolume = ((mx + 46.0) / 3.61)
				If ambientVolume < 0
					ambientVolume = 0
				ElseIf ambientVolume > 100
					ambientVolume = 100
				EndIf
				slX# = mx / 102.83435 + 0.16732133;((mx + 46.0) / 3.61) * .035105 - .28
				If slX < -.28
					slX = -.28
				ElseIf slX > 3.2305
					slX = 3.2305
				Else
					s5Min = mx - 4
					s5Max = mx + 4
				EndIf
				PositionEntity(sliderSprite5, slX, .17, 5)
			EndIf
			If MouseHit(1)
				If overSave
					SaveSettings()
					LoadSounds()
					Exit
				ElseIf overCancel
					Exit
				ElseIf my < 161 And my > 142 And mx > s3Min And mx < s3Max
					sl3Down = True
					sl4Down = False
					sl5Down = False
				ElseIf my < 98 And my > 79 And mx > s4Min And mx < s4Max
					sl3Down = False
					sl4Down = True
					sl5Down = False
				ElseIf my < 27 And my > 9 And mx > s5Min And mx < s5Max
					sl3Down = False
					sl4Down = False
					sl5Down = True
				ElseIf mx > 75 And my < -32 And mx < 158 And my > -51
					parrotSound = 1 - parrotSound
					If parrotSound = 1
						PositionEntity(sliderSprite, 1.382,  -.422, 5)
					Else
						PositionEntity(sliderSprite, .9,  -.422, 5)
					EndIf
				ElseIf mx > 75 And my < -109 And mx < 158 And my > -128
					windowed = 1 - windowed
					If windowed <> 1
						PositionEntity(sliderSprite2, 1.382, -1.143, 5)
					Else
						PositionEntity(sliderSprite2, .9, -1.143, 5)
					EndIf
				EndIf
			EndIf
			
			UpdateNormals(water)
			UpdateWorld()
			l_update();RenderWorld()
			DrawImage(mousedot,MouseX(),MouseY())
			;Text(0, 100, EntityX(mousesprite))
			;Text(0, 110, EntityY(mousesprite))
			;Text(0, 120, mx)
			;Text(0, 130, my)
			;Text(0, 140, EntityX(sliderSprite3))
			;Text(0, 150, s3Min)
			;Text(0, 160, s3Max)
			;Text(0, 170, "Slider3 Percentage: "+((EntityX(slidersprite3)+.28)/.035105))
			WaitTimer(timer)
			Flip
		Until KeyHit(1)
		If sliderSprite
			FreeEntity(sliderSprite)
			sliderSprite = 0
		EndIf
		If sliderSprite2
			FreeEntity(sliderSprite2)
			sliderSprite2 = 0
		EndIf
		If sliderSprite3
			FreeEntity(sliderSprite3)
			sliderSprite3 = 0
		EndIf
		If sliderSprite4
			FreeEntity(sliderSprite4)
			sliderSprite4 = 0
		EndIf
		If sliderSprite5
			FreeEntity(sliderSprite5)
			sliderSprite5 = 0
		EndIf
		If oSave
			FreeEntity(oSave)
			oSave = 0
		EndIf
		If oCancel
			FreeEntity(oCancel)
			oCancel = 0
		EndIf
		ShowEntity(titlesprite)
		ShowEntity(playsprite)
		ShowEntity(optionssprite)
		ShowEntity(creditssprite)
		ShowEntity(exitsprite)
		HideEntity(optionsmenusprite)
		FlushKeys()
		FlushJoy()
		FlushMouse()
	;;;;;;;;;;;
	; Credits ;
	;;;;;;;;;;;
	Case 3
		For parti.particlem=Each particlem
			FreeEntity(parti\entity)
			Delete parti
		Next
		FlushMouse()
		FlushKeys()
		FlushJoy()
		HideEntity(mousesprite)
		HideEntity(scoresButton)
		HideEntity(titlesprite)
		HideEntity(playsprite)
		HideEntity(optionssprite)
		HideEntity(creditssprite)
		HideEntity(exitsprite)
		ShowEntity(creditsName1)
		ShowEntity(creditsName2)
		ShowEntity(creditsName3)
		ShowEntity(creditsName4)
		ShowEntity(creditsName5)
		ShowEntity(creditsName6)
		PositionEntity(creditsName1,0,0,0)
		PositionEntity(creditsName2,0,0,0)
		PositionEntity(creditsName3,0,0,0)
		PositionEntity(creditsName4,0,0,0)
		PositionEntity(creditsName5, 0, 0, 0)
		PositionEntity(creditsName6, 0, 0, 0)
		MoveEntity(creditsName1,0,-5,5)
		MoveEntity(creditsName2,0,-10,5)
		MoveEntity(creditsName3,0,-16,5)
		MoveEntity(creditsName4,0,-21,5)
		MoveEntity(creditsName5, 0, -27, 5)
		MoveEntity(creditsName6, 0, -32, 5)
		creditsSpeed#=.01
		creditsTime=0
		Repeat
		
		creditsTime=creditsTime+1
		
		MoveEntity(creditsName1,0,creditsSpeed,0)
		MoveEntity(creditsName2,0,creditsSpeed,0)
		MoveEntity(creditsName3,0,creditsSpeed,0)
		MoveEntity(creditsName4,0,creditsSpeed,0)
		MoveEntity(creditsName5, 0, creditsSpeed, 0)
		MoveEntity(creditsName6, 0, creditsSpeed, 0)
		
		PositionEntity(skycube,EntityX(cam,True),EntityY(cam,True),EntityZ(cam,True))
		TurnEntity(pivot,0,.1,0)
		
		ph#=MilliSecs()/10.3       ;Frequency of waves (time)
		wtrvrtcnt%=CountVertices(watersurf)-1
			
		For k=0 To wtrvrtcnt Step 3            ;Loop through mesh and update vertices
			wtrvertx#=VertexX(watersurf,k)
			wtrvertz#=VertexZ(watersurf,k)
			wtrverty#=Sin(ph+wtrvertz*200)*wtrdepth ;Create wave
			VertexCoords watersurf,k,wtrvertx,wtrverty,wtrvertz ;Update
		Next
		UpdateNormals(water)
		
		If GetKey()
			Exit
		ElseIf GetMouse()
			Exit
		EndIf
		
		UpdateWorld()
		l_update();RenderWorld()
		Flip
		Until creditsTime>3700
		ShowEntity(mousesprite)
		ShowEntity(scoresButton)
		ShowEntity(titlesprite)
		ShowEntity(playsprite)
		ShowEntity(optionssprite)
		ShowEntity(creditssprite)
		ShowEntity(exitsprite)
		HideEntity(creditsName1)
		HideEntity(creditsName2)
		HideEntity(creditsName3)
		HideEntity(creditsName4)
		HideEntity(creditsName5)
		HideEntity(creditsName6)
		FlushMouse()
		FlushKeys()
		FlushJoy()
	;;;;;;;;
	; Exit ;
	;;;;;;;;
	Case 4
		For parti.particlem=Each particlem
			FreeEntity(parti\entity)
			Delete parti
		Next
		ClearWorld()
		EndGraphics()
		Exit
End Select
;
tRot=tRot+tDir
If tRot>.1
	tDir=-.1
ElseIf tRot<-8
	tDir=.1
EndIf
RotateSprite(titlesprite,tRot)
;
DrawImage(mousedot,MouseX(),MouseY())
;UpdateCubemapTextures(cam)
UpdateWorld()
l_update();RenderWorld()
;Text(0,0,tRot)
;Text(0,10,EntityX(creditsName1))
;Text(0,20,EntityYaw(creditsName1))
WaitTimer(timer)

Flip
Forever;Until KeyHit(1)
FreeSound(music)
For parti.particlem=Each particlem
	FreeEntity(parti\entity)
	Delete parti
Next
ClearWorld()
EndGraphics()
End
;;;;;;;;;;;;;;;;;;;;
; End of Main Loop ;
;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;
; Functions ;
;;;;;;;;;;;;;
Function SaveSettings()

file = WriteFile("settings.gfx")
WriteLine(file, width)
WriteLine(file, height)
WriteLine(file, depth)
If windowed = 0
	WriteLine(file, "1")
ElseIf mode <> 1
	WriteLine(file, mode)
Else
	WriteLine(file, 2)
EndIf
WriteLine(file, musicVolume)
WriteLine(file, sfxVolume)
WriteLine(file, ambientVolume)

WriteLine(file, parrotSound)
CloseFile(file)

End Function
;;;;;;;;;;;;;;;;;;;
; Skybox function ;
;;;;;;;;;;;;;;;;;;;
Function LoadSkyBox( file$,f_type%=1 )
	m=CreateMesh()
;	;
	If f_type=1
		;front face
		b=LoadBrush( file$+"_FR.jpg",49 )
		s=CreateSurface( m,b )
		AddVertex s,-1,+1,-1,0,0:AddVertex s,+1,+1,-1,1,0
		AddVertex s,+1,-1,-1,1,1:AddVertex s,-1,-1,-1,0,1
		AddTriangle s,0,1,2:AddTriangle s,0,2,3
		FreeBrush b
		;right face
		b=LoadBrush( file$+"_LF.jpg",49 )
		s=CreateSurface( m,b )
		AddVertex s,+1,+1,-1,0,0:AddVertex s,+1,+1,+1,1,0
		AddVertex s,+1,-1,+1,1,1:AddVertex s,+1,-1,-1,0,1
		AddTriangle s,0,1,2:AddTriangle s,0,2,3
		FreeBrush b
		;back face
		b=LoadBrush( file$+"_BK.jpg",49 )
		s=CreateSurface( m,b )
		AddVertex s,+1,+1,+1,0,0:AddVertex s,-1,+1,+1,1,0
		AddVertex s,-1,-1,+1,1,1:AddVertex s,+1,-1,+1,0,1
		AddTriangle s,0,1,2:AddTriangle s,0,2,3
		FreeBrush b
		;left face
		b=LoadBrush( file$+"_RT.jpg",49 )
		s=CreateSurface( m,b )
		AddVertex s,-1,+1,+1,0,0:AddVertex s,-1,+1,-1,1,0
		AddVertex s,-1,-1,-1,1,1:AddVertex s,-1,-1,+1,0,1
		AddTriangle s,0,1,2:AddTriangle s,0,2,3
		FreeBrush b
		;top face
		b=LoadBrush( file$+"_UP.jpg",49 )
		s=CreateSurface( m,b )
		AddVertex s,-1,+1,+1,0,1:AddVertex s,+1,+1,+1,0,0
		AddVertex s,+1,+1,-1,1,0:AddVertex s,-1,+1,-1,1,1
		AddTriangle s,0,1,2:AddTriangle s,0,2,3
		FreeBrush b
		;bottom face	
		b=LoadBrush( file$+"_DN.jpg",49 )
		s=CreateSurface( m,b )
		AddVertex s,-1,-1,-1,1,0:AddVertex s,+1,-1,-1,1,1
		AddVertex s,+1,-1,+1,0,1:AddVertex s,-1,-1,+1,0,0
		AddTriangle s,0,1,2:AddTriangle s,0,2,3
		;;;;;;;;;;;;;;;
	ElseIf f_type=2
		b=LoadBrush( file$+"_FR.bmp",49 )
		s=CreateSurface( m,b )
		AddVertex s,-1,+1,-1,0,0:AddVertex s,+1,+1,-1,1,0
		AddVertex s,+1,-1,-1,1,1:AddVertex s,-1,-1,-1,0,1
		AddTriangle s,0,1,2:AddTriangle s,0,2,3
		FreeBrush b
		;right face
		b=LoadBrush( file$+"_RT.bmp",49 )
		s=CreateSurface( m,b )
		AddVertex s,+1,+1,-1,0,0:AddVertex s,+1,+1,+1,1,0
		AddVertex s,+1,-1,+1,1,1:AddVertex s,+1,-1,-1,0,1
		AddTriangle s,0,1,2:AddTriangle s,0,2,3
		FreeBrush b
		;back face
		b=LoadBrush( file$+"_BK.bmp",49 )
		s=CreateSurface( m,b )
		AddVertex s,+1,+1,+1,0,0:AddVertex s,-1,+1,+1,1,0
		AddVertex s,-1,-1,+1,1,1:AddVertex s,+1,-1,+1,0,1
		AddTriangle s,0,1,2:AddTriangle s,0,2,3
		FreeBrush b
		;left face
		b=LoadBrush( file$+"_LF.bmp",49 )
		s=CreateSurface( m,b )
		AddVertex s,-1,+1,+1,0,0:AddVertex s,-1,+1,-1,1,0
		AddVertex s,-1,-1,-1,1,1:AddVertex s,-1,-1,+1,0,1
		AddTriangle s,0,1,2:AddTriangle s,0,2,3
		FreeBrush b
		;top face
		b=LoadBrush( file$+"_UP.bmp",49 )
		s=CreateSurface( m,b )
		AddVertex s,-1,+1,+1,0,1:AddVertex s,+1,+1,+1,0,0
		AddVertex s,+1,+1,-1,1,0:AddVertex s,-1,+1,-1,1,1
		AddTriangle s,0,1,2:AddTriangle s,0,2,3
		FreeBrush b
		;bottom face	
		b=LoadBrush( file$+"_DN.bmp",49 )
		s=CreateSurface( m,b )
		AddVertex s,-1,-1,-1,1,0:AddVertex s,+1,-1,-1,1,1
		AddVertex s,+1,-1,+1,0,1:AddVertex s,-1,-1,+1,0,0
		AddTriangle s,0,1,2:AddTriangle s,0,2,3
	EndIf
;	;
	FreeBrush b
	ScaleMesh m,100,100,100
	FlipMesh m
	EntityFX m,1
	Return m
End Function
;;;;;;;;;;;;;;;;;;;;;;;;;;
; End of Skybox Function ;
;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; Pre-Calculate Sine Values ;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
Function CalcSine()
;
For sval=0 To numofframes-1
	SineValue#(sval)=Sin(sval*10)
Next
;
End Function
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; End of Pre-Calculate Sine Values ;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;
; Load Menu Meshes :
;;;;;;;;;;;;;;;;;;;;
Function LoadMenu()
;
	tRot=0
	pRot=0
	oRot=0
	cRot=0
	eRot=0
	tDir=1
	pDir=1
	oDir=1
	cDir=1
	eDir=1
	creditsName1=CopyEntity(cN1,cam);LoadSprite("MenuObjects\Programmer.bmp",1+4)
	EntityOrder(creditsName1,-101)
	;EntityParent(creditsName1,cam)
	;PositionEntity(creditsName1,0,-8,5)
	ScaleSprite(creditsName1,4,1.33333)
	creditsName2=CopyEntity(cN2,cam);LoadSprite("MenuObjects\ShaneDaniels.bmp",1+4)
	EntityOrder(creditsName2,-101)
	;EntityParent(creditsName2,cam)
	;PositionEntity(creditsName2,0,-11,5)
	ScaleSprite(creditsName2,2,.6666666666)
	creditsName3=CopyEntity(cN3,cam);LoadSprite("MenuObjects\Artist.bmp",1+4)
	EntityOrder(creditsName3,-101)
	;EntityParent(creditsName3,cam)
	;PositionEntity(creditsName3,0,-8,5)
	ScaleSprite(creditsName3,4,1.33333)
	creditsName4=CopyEntity(cN4,cam);LoadSprite("MenuObjects\JesseMitchell.bmp",1+4)
	EntityOrder(creditsName4,-101)
	;EntityParent(creditsName4,cam)
	;PositionEntity(creditsName4,0,-11,5)
	ScaleSprite(creditsName4,2,.6666666666)
	creditsName5 = CopyEntity(cN5, cam)
	EntityOrder(creditsName5, -101)
	ScaleSprite(creditsName5, 4, 1.33333)
	creditsName6 = CopyEntity(cN6, cam)
	EntityOrder(creditsName6, -101)
	ScaleSprite(creditsName6, 4, 1.33333)
	;HideEntity(creditsName1)
	;HideEntity(creditsName2)
	;HideEntity(creditsName3)
	;HideEntity(creditsName4)
	;;;;;;;;;;;;;;;;;;;
	;       Blur      ;
	;;;;;;;;;;;;;;;;;;;
	l_surface=CreateSprite(cam)
	width=GraphicsWidth()
	height=GraphicsHeight();Global 
	
	SpriteViewMode l_surface,2
	PositionEntity l_surface,0,0,1.001
	EntityOrder l_surface,-90
	ScaleSprite l_surface, 1, 0.75
	EntityAlpha l_surface, .86
	
	l_texture=CreateTexture(width,Height,1+256)
	ScaleTexture l_texture, (Float TextureWidth(l_texture)/Float width),(Float TextureHeight(l_texture)/Float Height)
	
	EntityTexture l_surface,l_texture
	TextureBlend l_texture,2
	;;;;;;;;;;;;;;;;;;;
	;;;;;;;;;;;;;;;;;;;
	scoresButton = LoadAnimMesh("MenuObjects\coin.b3d")
	EntityParent(scoresButton, cam)
	PositionEntity(scoresButton, 14, -8, 30)
	RotateEntity(scoresButton, 0, 0, 0)
	ScaleEntity(scoresButton, .1, .1, .1)
	EntityColor(scoresButton, 255, 255, 255)
	Animate(scoresButton, 0)
	overScore = 0
	EntityOrder(FindChild(scoresButton,"Coin01"), -99)
	EntityOrder(FindChild(scoresButton,"CoinShine"), -100)
	particleentity=LoadSprite("Sprites\whitesprite.dds")
	EntityOrder(particleentity, -102)
	ScaleSprite(particleentity, 40, 40)
	HideEntity(particleentity)
	titlesprite=LoadSprite("MenuObjects\title.bmp",4)
	EntityOrder(titlesprite, -100)
	EntityParent(titlesprite,cam)
	PositionEntity(titlesprite, 0, 2, 5)
	ScaleSprite(titlesprite, 2.568561872, 1)
	playsprite=LoadSprite("MenuObjects\play.dds",4)
	EntityOrder(playsprite, -100)
	EntityParent(playsprite,cam)
	PositionEntity(playsprite, .1, -.4, 5)
	ScaleSprite(playsprite, 1, .5);.65, .65
	optionssprite=LoadSprite("MenuObjects\options.dds",4)
	EntityOrder(optionssprite, -100)
	EntityParent(optionssprite,cam)
	PositionEntity(optionssprite, 0, -1.3, 5)
	ScaleSprite(optionssprite, 1, .5);.75, .75
	creditssprite=LoadSprite("MenuObjects\credits.dds",4)
	EntityOrder(creditssprite, -100)
	EntityParent(creditssprite,cam)
	PositionEntity(creditssprite, 0, -2.2, 5)
	ScaleSprite(creditssprite, 1, .5);.75, .75
	exitsprite=LoadSprite("MenuObjects\exit.dds",4)
	EntityOrder(exitsprite, -100)
	EntityParent(exitsprite,cam)
	PositionEntity(exitsprite, 0, -3.1, 5)
	ScaleSprite(exitsprite, 1, .5);.5, .5
	mousesprite=LoadSprite("MenuObjects\mouse4.png",4)
	EntityOrder(mousesprite, -101)
	EntityParent(mousesprite,cam)
	PositionEntity(mousesprite, 0, -3, 5)
	ScaleSprite(mousesprite, 40, 40)
	mousedot=CreateImage(1,1)
	SetBuffer(ImageBuffer(mousedot))
	Rect(0,0,1,1)
	SetBuffer(BackBuffer())
	MoveMouse(mw,mh)
	optionsmenusprite=LoadSprite("MenuObjects\optionsmenu.bmp",1+4)
	EntityAlpha(optionsmenusprite, 1);.5 .78
	EntityOrder(optionsmenusprite,-100)
	EntityParent(optionsmenusprite,cam)
	PositionEntity(optionsmenusprite,0,0,5)
	ScaleSprite(optionsmenusprite,4,3);2,1.5)
	HideEntity(optionsmenusprite)
	skycube=LoadSkyBox("Skies\sky",1);Global
	EntityFX(skycube,1)
	EntityOrder(skycube,100)
	PositionEntity(skycube,EntityX(cam),EntityY(cam),EntityZ(cam))
	ScaleEntity(skycube,5,5,5)
	island=LoadMesh("MenuObjects\island.3ds")
	water=LoadMesh("MenuObjects\water.b3d")
	watersurf=GetSurface(water,1)
	EntityAlpha(water,1)
	watertex=LoadTexture("MenuObjects\watertex.png",1)
	caps=GfxDriverCaps3D()
	If caps<101
		waterref=LoadTexture("MenuObjects\waterrefs.jpg",1+64)
	Else
		waterref=LoadTexture("MenuObjects\waterref.jpg",1+128+256)
		SetCubeMode(waterref,1)
	EndIf
	PositionEntity(water,0,1,0)
	ScaleTexture(watertex,1,1)
	EntityTexture(water,waterref,0,0)
	EntityTexture(water,watertex,0,1)
	;MoveEntity(water,0,-1,0)
	;AddCubemap(water,water,1,1,512,128+256,0,0,1)
	optionselected%=0;Global
	;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	EntityAlpha(water,.67)
	;EntityFX(water,1)
	;EntityOrder(water,-98)
	EntityShininess(water,1)
	wtrdepth#=3
	ph#=MilliSecs()/10.3       ;Frequency of waves (time)
	wtrvrtcnt%=CountVertices(watersurf)-1
	
	For k=0 To wtrvrtcnt Step 3             ;Loop through mesh and update vertices
		wtrvertx#=VertexX(watersurf,k)
		wtrvertz#=VertexZ(watersurf,k)
		wtrverty#=Sin(ph+(wtrvertx/2)+(wtrvertz/2)*200)*wtrdepth ;Create wave
		VertexCoords watersurf,k,wtrvertx,wtrverty,wtrvertz ;Update
		;spheres(k)=CreateCube()
		;EntityOrder(spheres(k),-200)
		;PositionEntity(spheres(k),wtrvertx,wtrverty,wtrvertz)
	Next
	UpdateNormals(water)
	;WireFrame(True)
;
End Function
;;;;;;;;;;;;;;;;;;;;
; Free Menu Meshes ;
;;;;;;;;;;;;;;;;;;;;
Function FreeMenu()
;
FreeTexture(l_texture)
FreeEntity(l_surface)
;
FreeEntity(creditsName1)
FreeEntity(creditsName2)
FreeEntity(creditsName3)
FreeEntity(creditsName4)
FreeEntity(creditsName5)
FreeEntity(creditsName6)
;
FreeEntity(scoresButton)
FreeEntity(particleentity)
FreeEntity(titlesprite)
FreeEntity(playsprite)
FreeEntity(optionssprite)
FreeEntity(creditssprite)
FreeEntity(exitsprite)
FreeEntity(mousesprite)
FreeImage(mousedot)
FreeEntity(optionsmenusprite)
FreeEntity(skycube)
FreeEntity(island)
FreeEntity(water)
FreeTexture(watertex)
FreeTexture(waterref)
;
For parti.particlem=Each particlem
	FreeEntity(parti\entity)
	Delete parti
Next
;
End Function
;;;;;;;;;;;;;;;;;;;;
;       Blur       ;
;;;;;;;;;;;;;;;;;;;;
Function l_setAlpha(alpha#)

EntityAlpha l_surface,alpha#

End Function
;;;;;;;;;;;;;;;;;;;;
Function l_update(loc%=1, entity% = 0)

If entity = 0
	If loc=1
		RenderWorld
		CopyRect 0,0,width,height,0,0,BackBuffer(),TextureBuffer(l_texture)
	ElseIf loc=2
		RenderWorld
		CopyRect width-gameWidth-1,0,gameWidth,gameHeight,0,0,BackBuffer(),TextureBuffer(l_texture)
	EndIf
Else
	If loc=1
		RenderWorld
		CopyRect 0,0,width,height,0,0,BackBuffer(),TextureBuffer(entity)
	ElseIf loc=2
		RenderWorld
		CopyRect width-gameWidth-1,0,gameWidth,gameHeight,0,0,BackBuffer(),TextureBuffer(entity)
	EndIf
EndIf
;WaitKey()

End Function
;;;;;;;;;;;;;;;;;;;;
; End of Functions ;
;;;;;;;;;;;;;;;;;;;;