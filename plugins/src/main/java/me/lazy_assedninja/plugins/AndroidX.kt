package me.lazy_assedninja.plugins

object AndroidX {
    val appCompat = "androidx.appcompat:appcompat:${DependenciesVersions.appCompat}"
    val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${DependenciesVersions.constraintLayout}"
    val navigationFragment =
        "androidx.navigation:navigation-fragment:${DependenciesVersions.navigation}"
    val navigationUI = "androidx.navigation:navigation-ui:${DependenciesVersions.navigation}"
    val room = "androidx.room:room-runtime:${DependenciesVersions.room}"
    val roomCompiler = "androidx.room:room-compiler:${DependenciesVersions.room}"
    val viewPager2 = "androidx.viewpager2:viewpager2:${DependenciesVersions.viewPager2}"
    val swipeRefreshLayout =
        "androidx.swiperefreshlayout:swiperefreshlayout:${DependenciesVersions.swipeRefreshLayout}"

    val archCoreTesting = "androidx.arch.core:core-testing:${DependenciesVersions.archCore}"
    val testJunit = "androidx.test.ext:junit:${DependenciesVersions.testJunit}"
    val testEspresso = "androidx.test.espresso:espresso-core:${DependenciesVersions.testEspresso}"
}