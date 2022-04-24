echo OFF

set JAVA_HOME=C:\Program Files\Eclipse Foundation\jdk-16.0.2.7-hotspot\
set PATH=%JAVA_HOME%/bin;%PATH%
set VERSION=1.0.3
set DOCLET=com.tarsec.javadoc.pdfdoclet.PDFDoclet
set JARS="E:\SharedCo\Chess\ServerClient\ChessServerProj\pdfdoclet-1.0.3-all.jar"
set PACKAGE="cvu.html"

javadoc -docletpath "E:\aurigadoclet\bin\AurigaDoclet.jar"  -doclet com.aurigalogic.doclet.core.Doclet -encoding ISO-8859-1   -private  -sourcepath ./src -subpackages ver14 --ignore-source-errors -pdf out.pdf









