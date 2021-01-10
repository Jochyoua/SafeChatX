# SafeChatX
A plugin to keep your chat safe.

### SafeChat API
This plugin provides a simple API that allows users to program
their own checks and make SafeChat register them at runtime!
All checks define their own logic and behaviour using the passed data
from the chat events. The required data from the chat event is wrapped into a
"ChatData" object which contains the player, and the message objects.

All checks are based on the "Check" interface. The check interface is a simple
structure that allows you to define your own chat control structure. In this API
you will find most of the necessary classes and abstractions inside the 
"studio.thevipershow.safechat.api" package. 

To create a proper check, you must implement the check interface and define all the following methods respecting the scope:
#### (boolean) Check#check(ChatData)
This method will define the logic of your own check. When a check should fail,
you should make it return true, otherwise it should return false. When a check returns true
several other things will be done by SafeChat such as event cancelling and (not always) punishments and 
warnings.
#### (boolean) Check#hasWarningEnabled()
This specifies if your check, when failed, should provide a warning message to the user.
The warning message is defined through another method in the check interface.
#### (List<String>) Check#getWarningMessages()
This is the list of messages that should get displayed to the user upon failing a check
if the warning have been enabled. Each string in the list represents a newline and will
be sent individually. If you want an empty message please DO NOT return null,
return `Collections.emptyList()` or any other non-null empty list instead.
#### (String) Check#replacePlaceholders(String, ChatData)
This method is used by SafeChat in several places, and it allows your check to define custom placeholders
that can be later used into the configuration or elsewhere. The purpose of this method is
to return the same string provided from the first parameter, but with the placeholders replaced.
An example of this could be the following:
```java
    @Override
    public String replacePlaceholders(String message, ChatData data) {
        return message
            .replace("{PLAYER}", data.getPlayer().getName())
            .replace("{PREFIX}", SafeChat.PREFIX);
    }
```
#### (long) Check#getPunishmentRequiredValue()
This is a long value and indicates how many times does the player needs to fail
this specific check in order to trigger a punishment. If you do not want your check
to cause a punishment you should simply return **-1**. An example of this would be the 
following:
suppose you create a check and its punishment required value is 5, then the player will require
to fail this check 5 times in order for it to trigger a punishment. After the punishment has been
executed, the counter will restart from the beginning.
#### (String) Check#getPunishmentCommand()
This is the punishment that will be executed when the player fails enough time a check.
This messages are also subject to placeholders and they are defined through the 
replacePlaceholders method.
An example could be the following: `/kick {PLAYER}`
### Annotations API
SafeChat uses some annotations in order for you to easily pass information
into your checks without having to override methods.
SafeChat provides the **ChatCheck** abstract class, which takes advantage of annotations
and automatically overrides Check getName() and getPriority() for you.
Note that if you're using ChatCheck you are **FORCED** to use the following annotations:
#### @CheckName(name = String)
This annotations allows you to specify the name of that check.
Note that this same name will be used in the database.
#### @CheckPriority(priority = CheckPriority.Priority)
SafeChat implements a priority system, this means that check with a higher priority
will be executed first, and check with a low priority will be the last to be checked.

### Check registration
Now that we've explained how to define your own check, we need to register them.
To register checks you can find the **ChecksContainer** class, which uses the singleton
pattern. 
#### How to get this object?
As we've already said, this class uses the singleton pattern.
In order to get this instance you will first need to check two things:
First you want to add SafeChat as required dependency into your **plugin.yml**.
```yaml
name: 'MyPlugin'
version: '1.0.0-SNAPSHOT'
main: my.random.namespace.MyPlugin
api-version: '1.13'
depend: ['SafeChat'] # <-- This is the important line!
```
After having done this, you also want to double check and see if SafeChat has been loaded correctly.
To check if SafeChat is loaded correctly, you can simply do this from your code:
```java
public static boolean isSafechatLoaded() {
    return Bukkit.getPluginManager().getPlugin("SafeChat") != null;
}
```
Now, if you correctly followed this two steps, you will be able to register your own check 
using ChecksContainer.
To get the ChecksContainer you can call
`ChecksContainer.getInstance()` and if SafeChat was present and loaded this will not be null.

#### ChecksContainer Methods:
ChecksContainer defines several methods that allow you to interact with SafeChat checks.
#### (boolean) ChecksContainer#register(Check)
This method allows you to register a check by passing its instance.
After a check has been registered it will be immediately become available to SafeChat
and will start operating automatically. The return value of this function reports if the 
check has been registered correctly; a common case where this would return false is if
you were to try to register the same check twice, or tried to register a null check.
#### (boolean) ChecksContainer#unregister(Check)
This method allows you to unregister a check by passing its instance.
SafeChat will no longer keep checking it as long as it has been removed.
#### (Collection<Check>) getActiveChecks()
This method returns all the checks currently registered.
The checks are also returned sorted based on their priority.
### Check Example:
Now I've explained all of the basics to create and register a check,
here's a very simple example of a custom check that'd check message size:
```java
@CheckName(name = "MessageTooLong")
@CheckPriority(priority = CheckPriority.Priority.LOW)
public final class MessageTooLongCheck extends ChatCheck {
    
    public short maximumAllowedMessageLength = 32;
    public long myPunishmentAmount = 5;
    public boolean hasWarning = true; 
    public String punishment = "kick {PLAYER} Your message was {MSG_SIZE} but maximum allowed is {MAX_SIZE}!";
    
    private final List<String> myWarnings = Arrays.asList(
            "&8[&cWARNING&8]&7: &e{PLAYER}&7 your message was too long!",
            "&7The maximum allowed size is {MAX_SIZE}");
    
    @Override
    public boolean check(@NotNull ChatData data) {
        return data.getMessage().length() > maximumAllowedMessageLength;
    }

    @Override
    public boolean hasWarningEnabled() {
        return hasWarning;
    }

    @Override
    public @NotNull List<String> getWarningMessages() {
        return myWarnings;
    }

    @Override
    public @NotNull String replacePlaceholders(@NotNull String message, @NotNull ChatData data) {
        return message
                .replace("{PLAYER}", data.getPlayer().getName())
                .replace("{MSG_SIZE}", Integer.toString(message.length()))
                .replace("{MAX_SIZE}", Short.toString(maximumAllowedMessageLength));
    }

    @Override
    public long getPunishmentRequiredValue() {
        return myPunishmentAmount;
    }

    @Override
    public @NotNull String getPunishmentCommand() {
        return punishment;
    }
}
```

---
This guide finishes here, Thank you for reading!