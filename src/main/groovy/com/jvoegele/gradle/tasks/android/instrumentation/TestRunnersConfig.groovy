package com.jvoegele.gradle.tasks.android.instrumentation

/**
 * Configures the test runners used to run the instrumentation tests. This class basically
 * represents the 'runners' configure closure used in the build script.
 *
 * @author Matthias Kaeppler
 * @author Ladislav Thon
 */
class TestRunnersConfig {
  static final String PACKAGE    = "testpackage"
  static final String ANNOTATION = "annotation"
  static final String RUNNER     = "with"
  static final String NAME       = "name"
  static final String OPTIONS    = "options"

  def project
  def testPackage

  def defaultConfig
  def packageRunners    = [:]
  def annotationRunners = [:]

  def TestRunnersConfig(project, testPackage) {
    this.project = project
    this.testPackage = testPackage
  }

  def run(args = [:]) {
    createRunConfig(args)
  }

  def createRunConfig(args = [:]) {
    def testRunner  = args[(RUNNER)] ?: project.convention.plugins.android.instrumentationTestsRunner
    def packageName = args[(PACKAGE)]
    def annotation  = args[(ANNOTATION)]
    def name        = args[(NAME)] ?: "instrumentation-tests-$numRunners"
    def options     = args[(OPTIONS)] ?: []

    if (!options instanceof List) {
      options = [options]
    }

    // always wait for tests to finish
    options << "-w"
    // enable support for Zutubi's JUnit report test runner
    options += ["-e", "reportFilePath", "${name}.xml"]

    testRunner = expandFullyQualifiedName(testRunner)

    if (packageName) {
      packageRunners[expandFullyQualifiedName(packageName)] = buildRunner(testRunner, name, options)
    } else if (annotation) {
      annotationRunners[annotation] = buildRunner(testRunner, name, options)
    } else {
      defaultConfig = buildRunner(testRunner, name, options)
    }
  }

  boolean performDefaultRun() {
    packageRunners.isEmpty() && annotationRunners.isEmpty()
  }

  int getNumRunners() {
    packageRunners.size() + annotationRunners.size()
  }

  def buildRunner(def testRunner, def name, def options) {
    def wrapper = new Expando()
    wrapper.runner = testRunner
    wrapper.name = name
    wrapper.options = options
    wrapper
  }

  /**
   * Expand name according to Android convention: if it starts with a period ("."), it will be prepended
   * with package name from the manifest, otherwise it will be returned as is.
   */
  String expandFullyQualifiedName(String name) {
    if (name.startsWith(".")) {
      return "${testPackage}${name}"
    }
    return name;
  }
}
