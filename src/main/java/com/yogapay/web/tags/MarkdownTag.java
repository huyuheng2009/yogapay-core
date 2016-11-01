package com.yogapay.web.tags;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.commons.io.FileUtils;
import org.tautua.markdownpapers.Markdown;
import org.tautua.markdownpapers.parser.ParseException;

public class MarkdownTag extends TagSupport {

	private String path;

	@Override
	public int doStartTag() throws JspException {
		try {
			Markdown md = new Markdown();
			String fPath = pageContext.getServletContext().getRealPath(path);
			File file = new File(fPath);
			if (!file.exists()) {
				throw new JspException(String.format("文件[%s]不存在", path));
			}
			md.transform(new StringReader(FileUtils.readFileToString(file, "UTF8")), pageContext.getOut());
		} catch (IOException e) {
			throw new JspException(e.getMessage(), e);
		} catch (ParseException e) {
			throw new JspException(e.getMessage(), e);
		}
		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
