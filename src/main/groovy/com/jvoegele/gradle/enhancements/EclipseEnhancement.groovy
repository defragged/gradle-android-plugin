package com.jvoegele.gradle.enhancements

import org.gradle.api.Project 
import org.gradle.plugins.eclipse.model.BuildCommand 

class EclipseEnhancement extends GradlePluginEnhancement {
  public EclipseEnhancement(Project project) {
    super(project)
  }

  public void apply() {
    project.gradle.taskGraph.whenReady { taskGraph ->
      if (taskGraph.hasTask(':eclipse')) {

        def eclipseProject = project.tasks['eclipseProject']
        if (eclipseProject) {
          project.configure(eclipseProject) {
            natures 'com.android.ide.eclipse.adt.AndroidNature'
            def builders = new LinkedList(buildCommands)
            builders.addFirst(new BuildCommand('com.android.ide.eclipse.adt.PreCompilerBuilder'))
            builders.addFirst(new BuildCommand('com.android.ide.eclipse.adt.ResourceManagerBuilder'))
            builders.addLast(new BuildCommand('com.android.ide.eclipse.adt.ApkBuilder'))
            buildCommands = new ArrayList(builders)
          }
        }

        project.configure(project.eclipseClasspath) {
          containers 'com.android.ide.eclipse.adt.ANDROID_FRAMEWORK'
        }
      }
    }
  }

}
