@echo off
echo 1. Crear nuevo documento
curl.exe -u admin: -X PUT -H "Content-Type: application/xml" -d "<libro id='L006'><titulo>Nuevo</titulo><autor>Test</autor><anio>2025</anio><disponible>true</disponible></libro>" http://localhost:8080/exist/rest/db/biblioteca/libro_006.xml

echo.
echo 2. Actualizar documento completo
curl.exe -u admin: -X PUT -H "Content-Type: application/xml" -d "<libro id='L006'><titulo>Actualizado</titulo><autor>Autor Nuevo</autor><anio>2025</anio><disponible>false</disponible></libro>" http://localhost:8080/exist/rest/db/biblioteca/libro_006.xml

echo.
echo 3. Ejecutar XQuery Update parcial
curl.exe -u admin: -X POST -H "Content-Type: application/xml" -d "<query xmlns='http://exist.sourceforge.net/NS/exist'><text><![CDATA[update value doc('/db/biblioteca/libro_006.xml')//titulo with 'Titulo via REST']]></text></query>" http://localhost:8080/exist/rest/db

echo.
echo 4. Eliminar documento
curl.exe -u admin: -X DELETE http://localhost:8080/exist/rest/db/biblioteca/libro_006.xml

echo.
echo 5. Verificar eliminacion
curl.exe -u admin: -I http://localhost:8080/exist/rest/db/biblioteca/libro_006.xml

pause