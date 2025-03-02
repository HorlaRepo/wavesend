<#assign emailSubject = msg("eventUpdateTotpSubject")>
<#import "template.ftl" as layout>
<@layout.emailLayout emailSubject=emailSubject>
${kcSanitize(msg("eventUpdateTotpBodyHtml",event.date, event.ipAddress))?no_esc}
</@layout.emailLayout>
