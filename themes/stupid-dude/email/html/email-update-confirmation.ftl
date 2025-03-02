<#assign emailSubject = msg("emailUpdateConfirmationSubject")>
<#import "template.ftl" as layout>
<@layout.emailLayout emailSubject=emailSubject>
${kcSanitize(msg("emailUpdateConfirmationBodyHtml",link, newEmail, realmName, linkExpirationFormatter(linkExpiration)))?no_esc}
</@layout.emailLayout>
