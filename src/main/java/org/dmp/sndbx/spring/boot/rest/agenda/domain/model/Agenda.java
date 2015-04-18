package org.dmp.sndbx.spring.boot.rest.agenda.domain.model;

import java.io.Serializable;

import javax.persistence.Entity;

@Entity
public class Agenda extends AbstractEntity implements Serializable {
   private static final long serialVersionUID = 1L;

   private String ownerName;

   // @OneToMany
   // @JoinColumn(name = "ownerId")
   // // join column is in table for Customer
   // private List<Contacto> contactos;

   public Agenda(String ownerName) {
      super();
      this.ownerName = ownerName;
   }

   public Agenda() {
      // Constructor Vacio por JPA
   }

   public String getOwnerName() {
      return ownerName;
   }

   public void setOwnerName(String ownerName) {
      this.ownerName = ownerName;
   }

   @Override
   public String toString() {
      return "Agenda [id=" + this.getId() + ", ownerName=" + ownerName + "]";
   }
}
