package cn.yo2.aquarium.pocketvoa;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.content.Intent;
import android.os.Environment;

public class Utils {

	public static Article getArticleFromIntent(Intent intent) {
		Article article = new Article();

		article.id = intent.getLongExtra(Article.K_ID, -1);
		article.title = intent.getStringExtra(Article.K_TITLE);
		article.text = intent.getStringExtra(Article.K_TEXT);
		article.mp3 = intent.getStringExtra(Article.K_MP3);
		article.type = intent.getStringExtra(Article.K_TYPE);
		article.subtype = intent.getStringExtra(Article.K_SUBTYPE);
		article.url = intent.getStringExtra(Article.K_URL);
		article.date = intent.getStringExtra(Article.K_DATE);

		return article;
	}

	public static void putArticleToIntent(Article article, Intent intent) {
		intent.putExtra(Article.K_ID, article.id);
		intent.putExtra(Article.K_TITLE, article.title);
		intent.putExtra(Article.K_TEXT, article.text);
		intent.putExtra(Article.K_DATE, article.date);
		intent.putExtra(Article.K_TYPE, article.type);
		intent.putExtra(Article.K_SUBTYPE, article.subtype);
		intent.putExtra(Article.K_URL, article.url);
		intent.putExtra(Article.K_MP3, article.mp3);
	}

	public static String loadText(Article article) throws IOException {
		File downloadFile = localTextFile(article);
		FileReader fr = new FileReader(downloadFile);
		StringBuilder text = new StringBuilder();
		char[] buf = new char[1024];
		while (fr.read(buf) != -1) {
			text.append(buf);
		}
		return text.toString();
	}
	
	public static File localTextFile(Article article) {
		return new File(localArticleDir(article), Utils.extractFilename(article.url));
	}

	public static File localMp3File(Article article) {
		return new File(localArticleDir(article), Utils.extractFilename(article.mp3));
	}
	
	public static File localArticleDir(Article article) {
		File dir = new File("/sdcard/pocket-voa/" + article.type + '/' + article.subtype + '/');
		if (!dir.exists())
			dir.mkdirs();
		return dir;
	}

	public static File getAppDir() {
		if (isExternalStorageReady()) {
			File appDir = new File(Environment.getExternalStorageDirectory(),
					"pocket-voa");
			if (appDir.exists()) {
				return appDir;

			} else {
				if (appDir.mkdir())
					return appDir;
				else
					return null;
			}
		} else
			return null;
	}

	public static boolean isExternalStorageReady() {
		return (Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED));
	}

	public static String extractFilename(String url) {
		return url.substring(url.lastIndexOf('/') + 1);
	}

	/**
	 * Delete file or directory
	 * 
	 * @param fileName
	 *            the name of the file or directory to be deleted
	 * @return true if successful, false otherwise
	 */
	public static boolean delete(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			return false;
		} else {
			if (file.isFile()) {
				return deleteFile(fileName);
			} else {
				return deleteDirectory(fileName);
			}
		}
	}

	/**
	 * Delete a single file
	 * 
	 * @param fileName
	 *            the name of the file to be deleted
	 * @return true if successful, false otherwise
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.isFile() && file.exists()) {
			return file.delete();
		} else {
			return false;
		}
	}

	/**
	 * Recursive delete directory and files under it
	 * 
	 * @param dir
	 *            the directory to be deleted
	 * @return true if successful, false otherwise
	 */
	public static boolean deleteDirectory(String dir) {
		if (!dir.endsWith(File.separator)) {
			dir = dir + File.separator;
		}
		File dirFile = new File(dir);

		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;

		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {

			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}

			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
		}

		if (!flag) {
			return false;
		}

		// delete current directory
		return dirFile.delete();
	}

	/**
	 * convert date string from yyyyMMdd to yyyy-MM-dd
	 * 
	 * @param date
	 * @return the converted date string
	 */
	public static String convertDateString(String date) {
		StringBuilder sb = new StringBuilder(date);
		sb.insert(4, '-');
		sb.insert(7, '-');
		return sb.toString();
	}

	/**
	 * convert date string from /\d{2,4}-\d{1,2}-\d{1,2}/ to yyyyMMdd
	 * 
	 * @param date
	 * @return the formated date string
	 */
	public static String formatDateString(String date) {
		String[] parts = date.split("-", 3);
		StringBuilder sb = new StringBuilder();
		if (parts[0].length() == 2) {
			sb.append("20");
		}
		sb.append(parts[0]);
		if (parts[1].length() == 1) {
			sb.append('0');
		}
		sb.append(parts[1]);
		if (parts[2].length() == 1) {
			sb.append('0');
		}
		sb.append(parts[2]);
		return sb.toString();
	}
}
