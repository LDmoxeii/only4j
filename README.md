## 快速开始

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>io.github.netcorepal</groupId>
    <artifactId>cap4j-ddd-mvc-example</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>cap4j-ddd-mvc-example</name>
    <dependencies>
        <dependency>
            <groupId>io.github.netcorepal</groupId>
            <artifactId>cap4j-ddd-codegen-maven-plugin</artifactId>
            <version>3.1.0-alpha-1</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>io.github.netcorepal</groupId>
                <artifactId>cap4j-ddd-codegen-maven-plugin</artifactId>
                <version>3.1.0-alpha-1</version>
                <configuration>
                    <basePackage>com.only4</basePackage>
                    <archTemplate>https://raw.githubusercontent.com/LDmoxeii/only4j/refs/heads/main/cap4j-ddd-codegen-template-multi-nested.json</archTemplate>
                    <multiModule>true</multiModule>
                    <moduleNameSuffix4Adapter>-adapter</moduleNameSuffix4Adapter>
                    <moduleNameSuffix4Domain>-domain</moduleNameSuffix4Domain>
                    <moduleNameSuffix4Application>-application</moduleNameSuffix4Application>
                    <connectionString>
                        <![CDATA[jdbc:mysql://127.0.0.1:3306/only4-ksp-cap4j?serverTimezone=Asia\Shanghai&useSSL=false&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull]]>
                    </connectionString>
                    <user>root</user>
                    <pwd>123456</pwd>
                    <schema>only4-ksp-cap4j</schema>
                    <table></table>
                    <ignoreTable>\_\_%</ignoreTable>
                    <ignoreFields>db_%</ignoreFields>
                    <versionField>version</versionField>
                    <deletedField>del_flag</deletedField>
                    <readonlyFields>db_created_at,db_updated_at</readonlyFields>
                    <entityBaseClass></entityBaseClass>
                    <entityClassExtraImports>static org.netcorepal.cap4j.ddd.domain.event.DomainEventSupervisorSupport.events</entityClassExtraImports>
                    <entitySchemaOutputMode>ref</entitySchemaOutputMode>
                    <entitySchemaOutputPackage>domain._share.meta</entitySchemaOutputPackage>
                    <fetchType>LAZY</fetchType>
                    <idGenerator>org.netcorepal.cap4j.ddd.domain.distributed.SnowflakeIdentifierGenerator</idGenerator>
                    <enumValueField>code</enumValueField>
                    <enumNameField>name</enumNameField>
                    <enumUnmatchedThrowException>true</enumUnmatchedThrowException>
                    <datePackage4Java>java.time</datePackage4Java>
                    <typeRemapping></typeRemapping>
                    <generateDefault>false</generateDefault>
                    <generateDbType>true</generateDbType>
                    <generateSchema>true</generateSchema>
                    <generateParent>false</generateParent>
                    <aggregateRootAnnotation></aggregateRootAnnotation>
                    <aggregateRepositoryBaseClass></aggregateRepositoryBaseClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

```
