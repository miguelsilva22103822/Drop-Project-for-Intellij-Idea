package org.dropProject.dropProjectPlugin


import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import net.lingala.zip4j.ZipFile
import java.io.File
import javax.swing.JOptionPane

class ZipFolder(private val students: ArrayList<User>) {

    fun zipIt(e: AnActionEvent): String? {
        val projectDirectory = e.project?.let { FileEditorManager.getInstance(it).project.basePath.toString() }
        val separator = System.getProperty("file.separator")
        val newUploadFile = File("$projectDirectory${separator}projeto.zip")
        val authorsPath = "$projectDirectory${separator}AUTHORS.txt"
        val srcPath = "$projectDirectory${separator}src"
        val testsFilesPath = "$projectDirectory${separator}test-files"

        if (!File(authorsPath).exists()) {
            AuthorsFile(students).make(projectDirectory, true, e)
        }

        // Delete any existing zip file to ensure a new one is created
        if (newUploadFile.exists()) {
            newUploadFile.delete()
        }

        // Add AUTHORS.txt to a new zip
        ZipFile(newUploadFile)
            .addFile(File(authorsPath))

        return if (!File(srcPath).exists()) {
            JOptionPane.showMessageDialog(
                null, "Src Folder Not Found",
                "Submit Error",
                JOptionPane.ERROR_MESSAGE
            )
            null
        } else {
            val zipFile = ZipFile(newUploadFile)

            zipFile.addFolder(File(srcPath))

            if (File(testsFilesPath).exists()) {
                // Ask the user if they want to include the test files
                val includeTests = JOptionPane.showConfirmDialog(
                    null,
                    "Do you want to include the test files?",
                    "Include Test Files",
                    JOptionPane.YES_NO_OPTION
                )

                if (includeTests == JOptionPane.YES_OPTION) {
                    // Add the "test-files" folder to the existing zip
                    zipFile.addFolder(File(testsFilesPath))
                }
            }

            newUploadFile.path
        }
    }
}
