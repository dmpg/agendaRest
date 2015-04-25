package org.dmp.sndbx.spring.boot.rest.agenda.model.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Contacto extends AbstractEntity implements Serializable {
   private static final long serialVersionUID = 1L;

   private String contactName;
   private String email;
   private String tel;

   @ManyToOne(fetch = FetchType.EAGER)
   @JoinColumn(name="ownerId")
   private Agenda agenda;

   public Contacto() {
      // Constructor Vacio
   }

   public String getContactName() {
      return contactName;
   }

   public void setContactName(String contactName) {
      this.contactName = contactName;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getTel() {
      return tel;
   }

   public void setTel(String tel) {
      this.tel = tel;
   }

   public Agenda getAgenda() {
      return agenda;
   }

   public void setAgenda(Agenda agenda) {
      this.agenda = agenda;
   }

   @Override
   public String toString() {
      return "Contacto [id=" + this.getId()
            + ", contactName=" + contactName
            + ", email=" + email
            + ", tel=" + tel
            + ""
            + ", agenda=" + agenda
            + "]";
   }

}
