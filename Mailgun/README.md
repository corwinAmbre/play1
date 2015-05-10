# Mailgun plug-in #

Plugin for playframwork 1.x to ease integration of mailgun emails solution.

## Installation ##
1. Add plugin play1-mailgun to your dependencies
2. Add the twoo following declarations to your application.conf file:
   `mailgun.domain=your.mailgun.domain
    mailgun.api.key=your_mailgun_key`

## Sending an email ##
Create an `MailgunSendForm` object and fill mandatory fields:
* from: `String` email adress of the sender
* to: `List<String>` list of email adresses of recipients
* subject: `String` subject of the mail
* text or html: `String` content of the email (fill at least one of the two fields)

### Static method ###
Once form object is initialized, you can directly use the static method to send the email:
```Promise<Boolean> isSent = MailgunSender.send(form);```
Sending will be done asynchronously (using Job.now() method invocation), form object has to be valid (usage of `@Valid`)
It is not mandatory to use the result of the call since job launch is done.

### Job instanciation ###
`MailgunSender` extends `Job` class, you can use it as any Job class. In this case, form object validation is not used and should be performed prior to invoke now method.