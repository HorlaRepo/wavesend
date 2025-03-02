<#assign emailSubject = msg("emailTestSubject")>
<#import "template.ftl" as layout>
<@layout.emailLayout emailSubject=emailSubject>
${kcSanitize(msg("emailTestBodyHtml",realmName))?no_esc}
</@layout.emailLayout>
