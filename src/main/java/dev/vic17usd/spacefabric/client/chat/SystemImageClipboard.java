package dev.vic17usd.spacefabric.client.chat;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.lwjgl.system.Platform;

final class SystemImageClipboard {
    private SystemImageClipboard() { }
    static void copy(Path file) throws IOException {
        var image=ClipboardImage.fromPng(file);
        if(Platform.get()==Platform.WINDOWS){
            var window=net.minecraft.client.Minecraft.getInstance().getWindow();
            long owner=org.lwjgl.glfw.GLFWNativeWin32.glfwGetWin32Window(window.handle());
            WindowsImageClipboard.copy(owner,image.image(),Files.readAllBytes(file));return;
        }
        if(Platform.get()==Platform.MACOSX){
            command(List.of("/usr/bin/osascript","-e","on run argv\nset the clipboard to (read POSIX file (item 1 of argv) as «class PNGf»)\nend run",file.toAbsolutePath().toString()),null);return;
        }
        IOException last=null;
        for(var executable:List.of("wl-copy","xclip"))try{
            if(executable.equals("wl-copy")&&System.getenv("WAYLAND_DISPLAY")==null)continue;
            if(executable.equals("xclip")&&System.getenv("DISPLAY")==null)continue;
            command(executable.equals("wl-copy")?List.of(executable,"--type","image/png"):List.of(executable,"-selection","clipboard","-t","image/png"),file);return;
        }catch(IOException failure){last=failure;}
        if(!java.awt.GraphicsEnvironment.isHeadless()){
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(image,null);return;
        }
        throw new IOException("No native image clipboard provider is available",last);
    }
    private static void command(List<String> args,Path input) throws IOException {
        var builder=new ProcessBuilder(args).redirectOutput(ProcessBuilder.Redirect.DISCARD).redirectError(ProcessBuilder.Redirect.DISCARD);
        if(input!=null)builder.redirectInput(input.toFile());
        var process=builder.start();
        if(input==null)process.getOutputStream().close();
        try{
            if(!process.waitFor(5,TimeUnit.SECONDS)){process.destroyForcibly();throw new IOException("Clipboard helper timed out");}
            if(process.exitValue()!=0)throw new IOException("Clipboard helper failed: "+args.getFirst());
        }catch(InterruptedException interrupted){process.destroyForcibly();Thread.currentThread().interrupt();throw new IOException(interrupted);}
    }
}
