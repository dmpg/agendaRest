package org.dmp.sndbx.spring.boot.rest.agenda.model.validator;

import org.apache.commons.validator.EmailValidator;
import org.dmp.sndbx.spring.boot.rest.agenda.model.domain.Contact;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ContactValidator implements Validator {
   @Override
   public boolean supports(Class<?> clazz) {
      return Contact.class.isAssignableFrom(clazz);
   }

   @Override
   public void validate(Object target, Errors errors) {
      Contact contact = (Contact) target;
      if (!EmailValidator.getInstance().isValid(contact.getEmail())) {
         errors.rejectValue("email", "contact.email.invalid", "The eMail is not valid.");
      }
   }

}
