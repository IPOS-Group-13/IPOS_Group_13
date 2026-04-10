package com.teesolutions.ipospu.dto;

import com.teesolutions.ipospu.mail.SmtpDispatchOutcome;


public record OutboundEmailResult(boolean insertedIntoOutbox, SmtpDispatchOutcome smtp) {
}
