/*
 * Java Genetic Algorithm Library (jenetics-3.3.0).
 * Copyright (c) 2007-2015 Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.gradle.plugin

import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.Project
import org.jenetics.gradle.Version
import org.jenetics.gradle.task.Lyx2PDFTask

/**
 * Plugin which adds a build task for creating a PDF file from the lyx sources.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.5
 * @version 1.5
 */
class LyxPlugin extends JeneticsPlugin {

	private static final String BUILD = 'build'
	private static final String LYX = 'lyx'

	@Override
	public void apply(final Project project) {
		this.project = project

		if (hasLyxSources()) {
			applyLyx()
		}
	}

	private void applyLyx() {
		project.task(BUILD, dependsOn: LYX) << {
		}

		project.task('preparePDFGeneration') << {
			project.copy {
				from("${project.projectDir}/src/main") {
					include 'lyx/manual.lyx'
				}
				into project.build.temporaryDir
				filter(ReplaceTokens, tokens: [
					__year__: project.copyrightYear,
					__identifier__: project.manualIdentifier,
					__version__: project.version,
					__minor_version__: Version.parse(project.version.toString())
						.minorVersionString()
				])
			}
			project.copy {
				from("${project.projectDir}/src/main") {
					exclude 'lyx/manual.lyx'
				}
				into project.build.temporaryDir
			}
		}

		project.task(LYX, type: Lyx2PDFTask, dependsOn: 'preparePDFGeneration') {
			document = new File("${project.build.temporaryDir}/lyx/manual.lyx")

			doLast {
				project.copy {
					from "${project.build.temporaryDir}/lyx/manual.pdf"
					into "${project.buildDir}/doc"
					rename { String fileName ->
						fileName.replace('manual.pdf', "manual-${project.version}.pdf")
					}
				}
			}
		}

		project.task('clean') << {
			project.buildDir.deleteDir()
		}
	}

}
