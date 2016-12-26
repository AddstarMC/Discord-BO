package au.com.addstar.discord.managers;

import au.com.addstar.discord.objects.McUser;
import org.junit.Before;
import org.junit.Test;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.List;
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


    @Test
    public void checkUserDisplayNameTest() throws Exception {
        IUser user2 = mock(IUser.class);
        when(user2.getID()).thenReturn("654321");
        when(user2.getDisplayName(guild)).thenReturn("TestUser2");
        when(guild.getUserByID(user2.getID())).thenReturn(user2);
        McUser mUser = new McUser("654321");
        assertNull(mUser.getDisplayName(guild.getID()));
        UserManager.checkUserDisplayName(mUser,guild);
        UserManager.addGuildtoUser(mUser,"TestUser4",guild);
        assertEquals(mUser.getDisplayName(guild.getID()), "TestUser4");
        when(user2.getDisplayName(guild)).thenReturn("TestUser5");
        UserManager.checkUserDisplayName(mUser,guild);
        assertEquals(mUser.getDisplayName(guild.getID()), "TestUser4");
    }

    private IGuild guild;
     private IUser user;
     private List<IUser> iUsers;
     private List<IGuild> guilds = new ArrayList<IGuild>();
     private IDiscordClient client;

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

    @Test
    public void initializeTest() throws Exception {
        UserManager.initialize(client);
        assertEquals(client.getGuilds().get(0),guild);
        assertEquals(guild.getUsers().get(0), user);
        assertEquals(UserManager.loadUser("12345").getDisplayName(guild.getID()),"TestUser" );
    }


    @Before
    public void setUp() throws Exception {
        client = mock(IDiscordClient.class);
        when(client.getGuilds()).thenReturn(guilds);
        user=mock(IUser.class);
        iUsers = new ArrayList<IUser>();
        guild= mock(IGuild.class);
        iUsers.add(user);
        guilds.add(guild);
        when(user.getID()).thenReturn("12345");
        when(guild.getUsers()).thenReturn(iUsers);
        when(user.getDisplayName(guild)).thenReturn("TestUser");
        when(guild.getID()).thenReturn("2000000000");
        iUsers.add(user);


    }
}