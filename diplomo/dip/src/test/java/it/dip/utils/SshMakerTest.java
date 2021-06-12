package it.dip.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class SshMakerTest {
    @Test
    public void checkDockerInstanceTest() {
        SshMaker sshMaker = new SshMaker("oleg", "test", "test.ru");
        sshMaker.doCommand("sudo docker");
        Assert.assertTrue(sshMaker.getOutLog().substring(6, 10).equals("sage"));
    }
}