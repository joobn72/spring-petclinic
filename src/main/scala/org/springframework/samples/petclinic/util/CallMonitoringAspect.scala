/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.util

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.jmx.export.annotation.ManagedAttribute
import org.springframework.jmx.export.annotation.ManagedOperation
import org.springframework.jmx.export.annotation.ManagedResource
import org.springframework.util.StopWatch

/**
 * Simple aspect that monitors call count and call invocation time. It uses JMX annotations and therefore can be
 * monitored using any JMX console such as the jConsole
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Michael Isvy
 * @since 2.5
 */
@ManagedResource("petclinic:type=CallMonitor")
@Aspect
class CallMonitoringAspect {

  private var _enabled:Boolean = true

  private var _callCount:Int = 0

  private var _accumulatedCallTime:Long = 0


  @ManagedAttribute
  def setEnabled(enabled:Boolean) {
    _enabled = enabled
  }

  @ManagedAttribute
  def isEnabled = _enabled

  @ManagedOperation
  def reset {
    _callCount = 0
    _accumulatedCallTime = 0
  }

  @ManagedAttribute
  def getCallCount = _callCount

  @ManagedAttribute
  def getCallTime:Long = {
    if (_callCount > 0) {
      _accumulatedCallTime / _callCount
    } else {
      0
    }
  }


  @Around("within(@org.springframework.stereotype.Repository *)")
  def invoke(joinPoint:ProceedingJoinPoint):Object = {
    if (this._enabled) {
      val sw = new StopWatch(joinPoint.toShortString)

      sw.start("invoke")
      try {
        return joinPoint.proceed()
      } finally {
        sw.stop()
        this.synchronized {
          _callCount = _callCount + 1
          _accumulatedCallTime = _accumulatedCallTime + sw.getTotalTimeMillis
        }
      }
    } else {
      return joinPoint.proceed()
    }
  }

}