/*
 * Copyright 2019 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package kamon.newrelic

import com.newrelic.telemetry.Attributes
import com.typesafe.config.{Config, ConfigValue}
import kamon.tag.{Tag, TagSet}

object TagsToAttributes {
  def addTagsFromTagSets(tagSeq: Seq[TagSet], attributes: Attributes = new Attributes()): Attributes = {
    tagSeq.foreach { tagset: TagSet =>
      tagset.iterator().foreach(pair => {
        val value: Any = Tag.unwrapValue(pair)
        // Maintain the type of the tag value consistent with NR Attribute types
        value match {
          case value: String => attributes.put(pair.key, value)
          case value: Number => attributes.put(pair.key, value)
          case value: Boolean => attributes.put(pair.key, value)
        }
      })
    }
    attributes
  }

  def addTagsFromConfig(config: Config, attributes: Attributes = new Attributes()): Attributes = {
    config.entrySet().forEach(entry => {
      val key: String = entry.getKey
      val v = entry.getValue.unwrapped()
      v match {
        case v: String => attributes.put(key, v)
        case v: Number => attributes.put(key, v)
        case v: java.lang.Boolean => attributes.put(key, v)
      }
    })
    attributes
  }
}
