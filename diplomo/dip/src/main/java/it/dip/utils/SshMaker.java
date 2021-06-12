package it.dip.utils;

import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class SshMaker {
    private String user;
    private String password;
    private String host;
    private JSch jsch;
    private final java.util.Properties config;
    private Session session;
    private boolean status;
    private String outLog = "";

    public SshMaker(String user, String pas, String host) {
        this.user = user;
        this.host = host;
        this.password = pas;
        this.jsch = new JSch();
        this.config = new java.util.Properties();
        this.config.put("StrictHostKeyChecking", "no");
        this.status = true;
    }

    public boolean getStatus() {
        return this.status;
    }

    public String getOutLog() {
        return this.outLog;
    }

    public void forKubernetesFiles(String directory,String type,String project,String fileData){
        try{
            session=jsch.getSession(user,host);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();
            ChannelSftp channelSftp=(ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            try (OutputStream out = channelSftp.put(directory+"/"+type+"-"+project+".yaml")) {
                OutputStreamWriter writer = new OutputStreamWriter(out);
                writer.write(fileData);
                writer.flush();
                writer.close();
                channelSftp.disconnect();
                session.disconnect();
            } catch (IOException | SftpException e) {
                e.printStackTrace();
            }
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    public void forExternalDockerfile(String directory, String dockerfileData) {
        try {
            session = jsch.getSession(user, host);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();
            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            try (OutputStream out = channelSftp.put(directory + "/Dockerfile")) {
                OutputStreamWriter writer = new OutputStreamWriter(out);
                writer.write(dockerfileData);
                writer.flush();
                writer.close();
                channelSftp.disconnect();
                session.disconnect();
            } catch (IOException | SftpException e) {
                e.printStackTrace();
            }
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    public void forExternalDb(String dir, String dbDockerfileData, String fileType) {
        try {
            session = jsch.getSession(user, host);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();
            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            try (OutputStream out = channelSftp.put(dir + "/init." + fileType)) {
                OutputStreamWriter writer = new OutputStreamWriter(out);
                writer.write(dbDockerfileData);
                writer.flush();
                writer.close();
                channelSftp.disconnect();
                session.disconnect();
            } catch (IOException | SftpException e) {
                e.printStackTrace();
            }
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }


    public void doCommand(String command) {
        try {
            session = jsch.getSession(user, host);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("sudo -S -p ' ' " + command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();
            ((ChannelExec) channel).setPty(true);
            channel.connect();
            OutputStream out = channel.getOutputStream();
            out.write((password + "\n").getBytes());
            out.flush();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                    outLog += new String(tmp, 0, i);
                }
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    this.status = true;
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                    this.status = false;
                }
            }
            channel.disconnect();
            session.disconnect();
        } catch (JSchException | IOException e) {
            this.status = false;
            e.printStackTrace();
        }
    }

}
