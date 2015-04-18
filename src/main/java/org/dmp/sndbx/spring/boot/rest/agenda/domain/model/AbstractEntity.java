package org.dmp.sndbx.spring.boot.rest.agenda.domain.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

@MappedSuperclass
public abstract class AbstractEntity {
   @Id
   @GeneratedValue
   @JsonIgnore
   // @JsonIgnore is to be ignored by introspection-based serialization and
   // deserialization functionality. That is, it should not be consider a
   // "getter", "setter" or "creator".
   private Long id;

   // ETag
   @Version
   @JsonIgnore
   private Long version;

   public Long getVersion() {
      if (this.version == null) {
         this.version = 0L;
      }

      return version;
   }

   public Long getId() {
      return id;
   }
}
