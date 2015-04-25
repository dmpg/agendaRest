package org.dmp.sndbx.spring.boot.rest.agenda.model.validator;

import org.apache.commons.validator.EmailValidator;
import org.dmp.sndbx.spring.boot.rest.agenda.model.domain.Contacto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ContactoValidator implements Validator {
   @Override
   public boolean supports(Class<?> clazz) {
      return Contacto.class.isAssignableFrom(clazz);
   }

   @Override
   public void validate(Object target, Errors errors) {
      Contacto contacto = (Contacto) target;
      if (!EmailValidator.getInstance().isValid(contacto.getEmail())) {
         errors.rejectValue("email", "contacto.email.invalid", "El eMail indicado es invalido.");
      }
   }

}
