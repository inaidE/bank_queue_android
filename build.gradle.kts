// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.9.3" apply false
    kotlin("android")                      version "1.8.22" apply false
    id("com.google.dagger.hilt.android")     version "2.49"   apply false
    kotlin("kapt")                           version "1.8.22" apply false
}