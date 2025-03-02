<#import "template.ftl" as layout>
<#import "user-profile-commons.ftl" as userProfileCommons>
<#import "register-commons.ftl" as registerCommons>

<@layout.registrationLayout displayMessage=messagesPerField.exists('global') displayRequiredFields=true; section>
    <#if section == "header">
        ${msg("registerTitle")}
    <#elseif section == "form">
        <form id="kc-register-form" class="${properties.kcFormClass!}" action="${url.registrationAction}" method="post">

            <@userProfileCommons.userProfileFormFields; callback, attribute>
                <#if callback == "afterField">
                    <#if attribute.name == "dob">
                        <div class="${properties.kcFormGroupClass!}">
                            <label for="dob" class="${properties.kcLabelClass!}">
                                <span class="pf-v5-c-form__label-text">
                                    ${msg("dob")}
                                    <span class="pf-v5-c-form__label-required" aria-hidden="true">&#42;</span>
                                </span>
                            </label>
                            <input type="date" id="dob" name="dob" class="${properties.kcInputClass!}" aria-invalid="<#if messagesPerField.existsError('dob')>true</#if>">
                            <#if messagesPerField.existsError('dob')>
                                <span id="input-error-dob" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                                    ${kcSanitize(messagesPerField.get('dob'))?no_esc}
                                </span>
                            </#if>
                        </div>
                    </#if>
                </#if>
            </@userProfileCommons.userProfileFormFields>

            <!-- Include your existing password fields and other form elements here -->

            <@registerCommons.termsAcceptance/>

            <#if recaptchaRequired??>
                <div class="form-group">
                    <div class="${properties.kcInputWrapperClass!}">
                        <div class="g-recaptcha" data-size="compact" data-sitekey="${recaptchaSiteKey}"></div>
                    </div>
                </div>
            </#if>

            <div class="${properties.kcFormGroupClass!}">
                <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                    <div class="${properties.kcFormOptionsWrapperClass!}">
                        <span><a href="${url.loginUrl}">${kcSanitize(msg("backToLogin"))?no_esc}</a></span>
                    </div>
                </div>

                <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                    <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" type="submit" value="${msg("doRegister")}"/>
                </div>
            </div>
        </form>
        <script type="text/javascript">
            document.getElementById('kc-register-form').addEventListener('submit', function(event) {
                var dobInput = document.getElementById('dob');
                if (dobInput.value) {
                    // Format date to yyyy-mm-dd
                    var dobValue = new Date(dobInput.value);
                    var formattedDate = dobValue.getFullYear() + '-' + ('0' + (dobValue.getMonth() + 1)).slice(-2) + '-' + ('0' + dobValue.getDate()).slice(-2);
                    dobInput.value = formattedDate;
                }
            });
        </script>
    </#if>
</@layout.registrationLayout>
