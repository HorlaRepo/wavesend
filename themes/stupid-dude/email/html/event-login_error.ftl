<#assign emailSubject = msg("eventLoginErrorSubject")>
<#import "template.ftl" as layout>
<@layout.emailLayout emailSubject=emailSubject>
${kcSanitize(msg("eventLoginErrorBodyHtml",event.date,event.ipAddress))?no_esc}
</@layout.emailLayout>
