@echo off
rem The version of Repast Simphony being used.
set VERSION=2.3.1

rem The installed path of Repast. Quotes may be necessary if there is a space character in the path.
set REPAST="D:\Repast Simphony\RepastSimphony-2.3.1"

rem The installed path of Eclipse. Quotes may be necessary if there is a space character in the path.
set ECLIPSE="D:\Repast Simphony\RepastSimphony-2.3.1\eclipse"

rem The plugins path of Eclipse.
set PLUGINS=%ECLIPSE%\plugins

rem The workspace containing the Repast model.
set WORKSPACE="C:\Users\Antonie\Java\workspace_repast\BosBrand"

rem The name of the model. This might be case-sensitive. This is the name of your package. It should be the package at the top of all your .java files and match the "package" listed in your model.score file (when viewed as a text file).
set MODELNAME=BosBrand

rem The folder of the model. This might be case-sensitive. This is the base folder of your project in the file system.
set MODELFOLDER=%WORKSPACE%

rem The file containing the batch parameters.
set BATCHPARAMS=%MODELFOLDER%\batch\batch_params.xml

rem Execute in batch mode.
java -cp %PLUGINS%\repast.simphony.batch_%VERSION%\bin;%PLUGINS%\repast.simphony.runtime_%VERSION%\lib\*;%PLUGINS%\repast.simphony.core_%VERSION%\lib\*;%PLUGINS%\repast.simphony.core_%VERSION%\bin;%PLUGINS%\repast.simphony.bin_and_src_%VERSION%\*;%PLUGINS%\repast.simphony.score.runtime_%VERSION%\lib\*;%PLUGINS%\repast.simphony.data_%VERSION%\lib\*;%MODELFOLDER%\bin repast.simphony.batch.BatchMain -params %BATCHPARAMS% %MODELFOLDER%\%MODELNAME%.rs