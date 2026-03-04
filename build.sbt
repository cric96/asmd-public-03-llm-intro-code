ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

lazy val root = (project in file("."))
  .settings(
    name := "asmd-llm-code",
    libraryDependencies += "dev.langchain4j" % "langchain4j" % "1.11.0",
    libraryDependencies += "dev.langchain4j" % "langchain4j-ollama" % "1.11.0",
    libraryDependencies += "dev.langchain4j" % "langchain4j-google-ai-gemini" % "1.11.0",
    libraryDependencies += "com.github.haifengl" %% "smile-scala" % "4.3.0",
    libraryDependencies += "com.github.haifengl" %% "smile-scala" % "4.3.0",
    libraryDependencies += "com.github.haifengl" % "smile-plot" % "4.3.0",
    libraryDependencies += "net.aichler" % "jupiter-interface" % "0.11.1" % Test,
    libraryDependencies += "org.junit.jupiter" % "junit-jupiter-api" % "5.10.2" % Test,
    libraryDependencies += "org.junit.jupiter" % "junit-jupiter-engine" % "5.10.2" % Test,
    libraryDependencies += "org.mockito" % "mockito-core" % "5.11.0" % Test,
    libraryDependencies += "org.mockito" % "mockito-junit-jupiter" % "5.11.0" % Test
  )
