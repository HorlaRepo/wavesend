<#assign emailSubject = msg("passwordResetSubject")>
<#import "template.ftl" as layout>
<@layout.emailLayout emailSubject=emailSubject>
${kcSanitize(msg("passwordResetBodyHtml",link, linkExpiration, realmName, linkExpirationFormatter(linkExpiration)))?no_esc}
</@layout.emailLayout>
