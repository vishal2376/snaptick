plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id("com.google.devtools.ksp")
	kotlin("kapt")
	id("com.google.dagger.hilt.android")
}

android {
	namespace = "com.vishal2376.snaptick"
	compileSdk = 34

	defaultConfig {
		applicationId = "com.vishal2376.snaptick"
		minSdk = 26
		targetSdk = 34
		versionCode = 11
		versionName = "3.3"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}

	signingConfigs {
		create("release") {
			// Env vars (CI) take precedence over local ~/.gradle/gradle.properties.
			fun signingProp(name: String): String? {
				return (System.getenv(name) ?: project.findProperty(name) as String?)
					?.takeIf { it.isNotBlank() }
			}

			val ksPath = signingProp("SNAPTICK_KEYSTORE_FILE")
			val ksPassword = signingProp("SNAPTICK_KEYSTORE_PASSWORD")
			val kAlias = signingProp("SNAPTICK_KEY_ALIAS")
			val kPassword = signingProp("SNAPTICK_KEY_PASSWORD")

			val ksFile = ksPath?.let { file(it) }?.takeIf { it.exists() }
			if (ksFile != null && ksPassword != null && kAlias != null && kPassword != null) {
				storeFile = ksFile
				storePassword = ksPassword
				keyAlias = kAlias
				keyPassword = kPassword
			}
			// Otherwise fields stay null. The fail-fast block below catches it.
		}
	}

	buildTypes {
		release {
			isMinifyEnabled = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
			signingConfig = signingConfigs.getByName("release")
		}

		debug {
			applicationIdSuffix = ".debug"
			versionNameSuffix = "-debug"
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
	buildFeatures {
		compose = true
		buildConfig = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.11"
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}

	ksp {
		arg(
			"room.schemaLocation",
			"$projectDir/schemas"
		)
	}

	testOptions {
		unitTests.isReturnDefaultValues = true
	}

	// Expose Room schemas to androidTest for MigrationTestHelper.
	sourceSets.getByName("androidTest").assets.srcDirs("$projectDir/schemas")
}

// Fail fast on misconfigured release builds. Catches:
// - Forgotten env vars in CI.
// - Local clones that don't have ~/.gradle/gradle.properties set.
// - Anyone trying to ship a release APK signed with the public Android debug key.
//
// Hooked into gradle.taskGraph.whenReady so the guard fires BEFORE any release
// task (including R8 / minify / package) starts, instead of after they've
// already burned a couple of minutes of CPU.
gradle.taskGraph.whenReady {
	val isReleaseBuild = allTasks.any {
		it.path.startsWith(":app:assembleRelease") ||
			it.path.startsWith(":app:bundleRelease") ||
			it.path == ":app:packageRelease"
	}
	if (!isReleaseBuild) return@whenReady

	val sc = android.signingConfigs.getByName("release")
	val storeFile = sc.storeFile
	val alias = sc.keyAlias
	require(storeFile != null && storeFile.exists()) {
		"Release keystore not configured. Set SNAPTICK_KEYSTORE_FILE in " +
			"~/.gradle/gradle.properties (local) or as an env var (CI)."
	}
	require(alias != null && alias != "androiddebugkey") {
		"Refusing to sign release with the public Android debug key alias."
	}
}


dependencies {

	implementation("androidx.core:core-ktx:1.12.0")
	implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
	implementation("androidx.activity:activity-compose:1.8.2")
	implementation(platform("androidx.compose:compose-bom:2023.08.00"))
	implementation("androidx.compose.ui:ui")
	implementation("androidx.compose.ui:ui-graphics")
	implementation("androidx.compose.ui:ui-tooling-preview")
	implementation("androidx.compose.material3:material3")

	//room
	implementation("androidx.room:room-runtime:2.6.1")
	annotationProcessor("androidx.room:room-compiler:2.6.1")
	implementation("androidx.room:room-ktx:2.6.1")
	ksp("androidx.room:room-compiler:2.6.1")

	//hilt
	implementation("com.google.dagger:hilt-android:2.49")
	kapt("com.google.dagger:hilt-android-compiler:2.49")
	kapt("androidx.hilt:hilt-compiler:1.2.0")
	implementation("androidx.hilt:hilt-work:1.2.0")
	implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

	//navigation
	implementation("androidx.navigation:navigation-compose:2.7.6")

	//lifecycle
	implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

	//time picker
	implementation("com.github.commandiron:WheelPickerCompose:1.1.11")

	//material icons extended
	implementation("androidx.compose.material:material-icons-extended:1.5.4")

	//acra - crash reports
	implementation("ch.acra:acra-mail:5.11.3")
	implementation("ch.acra:acra-dialog:5.11.3")

	//work manager
	implementation("androidx.work:work-runtime-ktx:2.9.0")

	//data store
	implementation("androidx.datastore:datastore-preferences:1.0.0")

	//splash screen
	implementation("androidx.core:core-splashscreen:1.0.1")

	//gson
	implementation("com.google.code.gson:gson:2.10.1")

	//calender
	implementation("com.kizitonwose.calendar:compose:2.4.1")

	//widget
	implementation("androidx.glance:glance-appwidget:1.1.1")
	implementation("androidx.glance:glance-material3:1.1.1")


	testImplementation("junit:junit:4.13.2")
	testImplementation("app.cash.turbine:turbine:1.1.0")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
	testImplementation("androidx.arch.core:core-testing:2.2.0")
	testImplementation("io.mockk:mockk:1.13.8")

	androidTestImplementation("androidx.test.ext:junit:1.1.5")
	androidTestImplementation("androidx.test:runner:1.5.2")
	androidTestImplementation("androidx.test:rules:1.5.0")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
	androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
	androidTestImplementation("androidx.compose.ui:ui-test-junit4")
	androidTestImplementation("androidx.room:room-testing:2.6.1")
	androidTestImplementation("androidx.work:work-testing:2.9.0")
	androidTestImplementation("com.google.dagger:hilt-android-testing:2.49")
	kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.49")
	androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
	androidTestImplementation("com.google.guava:guava:32.0.1-android")

	debugImplementation("androidx.compose.ui:ui-tooling")
	debugImplementation("androidx.compose.ui:ui-test-manifest")
}