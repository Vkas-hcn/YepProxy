@echo // Copyright (C) 1996-2017 Markus F.X.J. Oberhumer
@echo //
@echo //   Windows 32-bit
@echo //   Metrowerks CodeWarrior C/C++
@echo //
@call b\prepare.bat
@if "%BECHO%"=="n" echo off


set CC=mwcc -gccinc
set CF=-opt full %CFI% %CFASM%
set LF=%BLIB% -lwinmm.lib

%CC% -w on %CF% -w nounusedexpr -c @b\src.rsp
@if errorlevel 1 goto error
mwld -library -o %BLIB% @b\win32\vc.rsp
@if errorlevel 1 goto error

%CC% %CF% examples\dict.c %LF%
@if errorlevel 1 goto error
%CC% %CF% examples\lzopack.c %LF%
@if errorlevel 1 goto error
%CC% %CF% examples\precomp.c %LF%
@if errorlevel 1 goto error
%CC% %CF% examples\precomp2.c %LF%
@if errorlevel 1 goto error
%CC% %CF% examples\simple.c %LF%
@if errorlevel 1 goto error

%CC% %CF% lzotest\lzotest.c %LF%
@if errorlevel 1 goto error

%CC% %CF% -Iinclude\lzo minilzo\testmini.c minilzo\minilzo.c
@if errorlevel 1 goto error


@call b\done.bat
@goto end
:error
@echo ERROR during build!
:end
@call b\unset.bat
