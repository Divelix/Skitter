package com.divelix.skitter.examples

import com.badlogic.gdx.Gdx
import com.divelix.skitter.GdxTestRunner
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(GdxTestRunner::class)
class AssetExistsExampleTest {
    @Test
    fun badlogicLogoFileExists() {
        Assert.assertTrue("This test will only pass when the badlogic.jpg file coming with a new project setup has not been deleted.", Gdx.files
                .internal("../android/assets/badlogic.jpg").exists())
    }
}