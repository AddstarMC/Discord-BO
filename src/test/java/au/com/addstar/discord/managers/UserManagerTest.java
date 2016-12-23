package au.com.addstar.discord.managers;

import au.com.addstar.discord.objects.McUser;
import org.junit.Before;
import org.junit.Test;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.util.Objects;

import static au.com.addstar.discord.managers.UserManager.saveUser;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 19/12/2016.
 */
public class UserManagerTest {

     private IGuild guild;
     private IUser user;


    @Test
    public void addGuildtoUser() throws Exception {
        McUser testUser = new McUser(user.getID());
        UserManager.addGuildtoUser(testUser,"TesterUser",guild);
        assertTrue(Objects.equals(testUser.getDisplayName(guild.getID()), "TesterUser"));
    }

    @Test
    public void cacheUserTest() throws Exception {

    }

    @Test
    public void removeUserTest() throws Exception {

    }

    @Test
    public void saveUserTest() throws Exception {
        McUser testUser = new McUser(user.getID());
        testUser.addUpdateDisplayName(guild.getID(),"TestUser");
        saveUser(testUser);
        McUser newUser =  UserManager.loadUser(testUser.getDiscordID());
        assertEquals(newUser.getDisplayName(guild.getID()),testUser.getDisplayName(guild.getID()));
        UserManager.removeUser(newUser);
        McUser newUSer2 =  UserManager.loadUser(testUser.getDiscordID());
        assertEquals(newUSer2.getDisplayName(guild.getID()),testUser.getDisplayName(guild.getID()));

    }



    @Before
    public void setUp() throws Exception {
        user=mock(IUser.class);
        when(user.getID()).thenReturn("12345");
        guild= mock(IGuild.class);
        when(guild.getID()).thenReturn("2000000000");

    }
}