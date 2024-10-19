package com.kingcent.afast.utils;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.util.FS;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * @author rainkyzhong
 * @date 2024/10/13 0:10
 */
public class GitUtil {

    private static SshSessionFactory sshSessionFactory(String key){
        try {
            File file = File.createTempFile("key","a");
            FileWriter writer = new FileWriter(file);
            writer.write(key);
            writer.close();
            return new JschConfigSessionFactory() {
                @Override
                protected void configure(OpenSshConfig.Host host, Session session ) {
                    session.setConfig("StrictHostKeyChecking","no");
                }
                @Override
                protected JSch createDefaultJSch(FS fs) throws JSchException {
                    JSch sch = super.createDefaultJSch(fs);
                    sch.addIdentity(file.getAbsolutePath());
                    return sch;
                }
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Git open(String localRepoPath) {
        try {
            return Git.open(new File(localRepoPath));
        }catch (Exception e){
            return null;
        }
    }

    public static Collection<Ref> branches(String key, String url){

        SshSessionFactory sshSessionFactory = sshSessionFactory(key);
        LsRemoteCommand cmd = Git.lsRemoteRepository();
        try{
            return cmd.setRemote(url)
                    .setTransportConfigCallback(transport -> {
                        SshTransport sshTransport = (SshTransport) transport;
                        sshTransport.setSshSessionFactory(sshSessionFactory);
                    }).call();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    public static void clone(String remoteRepoPath, String localRepoPath, String keyPath) throws GitAPIException {
        SshSessionFactory sshSessionFactory = sshSessionFactory(keyPath);
        CloneCommand cloneCommand = Git.cloneRepository();
        try(
            Git ignored = cloneCommand.setURI(remoteRepoPath)
                    .setTransportConfigCallback(transport -> {
                        SshTransport sshTransport = (SshTransport) transport;
                        sshTransport.setSshSessionFactory(sshSessionFactory);
                    }).setDirectory(new File(localRepoPath))
                    .call();
        ){}
    }
}
