package com.composum.pages.options.microsite.service;

import java.text.MessageFormat;
import java.util.List;

/**
 * the import status state object and result
 */
public interface MicrositeImportStatus {

	/**
	 * @return 'true' if no error has occurred during import
	 */
	boolean isSuccessful();

	/**
	 * the message error level values
	 */
	enum MessageLevel {
		info, warn, error
	}

	/**
	 * a message object to collect information during import
	 */
	class Message {

		private final MessageLevel level;
		private final String text;
		private final Object[] args;

		public Message(MessageLevel level, String text, Object... args) {
			this.level = level;
			this.text = text;
			this.args = args;
		}

		public MessageLevel getLevel() {
			return level;
		}

		@Override
		public String toString() {
			return MessageFormat.format(text, args);
		}

		public Exception getException() {
			if (args != null) {
				for (Object arg : args) {
					if (arg instanceof Exception) {
						return (Exception) arg;
					}
				}
			}
			return null;
		}
	}

	/**
	 * @return the messages collected during the import
	 */
	List<Message> getMessages();
}
