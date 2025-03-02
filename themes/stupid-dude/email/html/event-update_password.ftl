<#assign emailSubject = msg("eventUpdatePasswordSubject")>
<#import "template.ftl" as layout>
<@layout.emailLayout emailSubject=emailSubject>
${kcSanitize(msg("eventUpdatePasswordBodyHtml",event.date, event.ipAddress))?no_esc}
</@layout.emailLayout>
