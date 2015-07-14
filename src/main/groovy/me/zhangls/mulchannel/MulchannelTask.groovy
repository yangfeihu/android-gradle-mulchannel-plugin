package me.zhangls.mulchannel

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class MulchannelTask extends DefaultTask {

    {
    group = 'mulchannel'
    description = """Gradle plugin for generating multiple channel apks
    See https://github.com/ihrthk/android-gradle-mulchannel-plugin#configuration for more information.
    Available configuration options and default values:
    //source apk file
    File inputFile
    //decompress dir
    File tempDir
    //output dir
    File outputDir
    //multiple channel
    List<String> channels"""
    }

    File DEFAULT_TEMP = new File(project.getProjectDir(), "temp")

    @TaskAction
    def mulchannel() {
        //1.get config extension
        ConfigExtension config = project.mulchannel
        //2.decompression zip file to tempDir
        def ant = new AntBuilder();

        def temp = config.tempDir == null ? DEFAULT_TEMP : config.tempDir;
        ant.unzip(src: config.inputFile, dest: temp)
        //3.iterater channels
        config.channels.each { item ->
            //3.1.new a channel file
            File file = new File(new File(temp, "META-INF"), "mulchannel_" + item)
            file.createNewFile()
            //3.2.zip the temp dir
            ant.zip(destfile: new File(config.outputDir, item + ".apk"), basedir: temp.getName());
            //3.3.delete channel file
            file.delete()
        }
        if (config.tempDir == null) {
            DEFAULT_TEMP.deleteDir();
        }
    }
}
