//npm install 即可，如果不行再使用下面
//gulp 需要全局装
//npm install gulp@4.0.2 -g
//新安装依赖命令后面加上 --save-dev
var gulp = require('gulp');
var fileinclude  = require('gulp-file-include');
var replace = require('gulp-replace');
// node 删除插件
var del  = require('del');
var express = require('express');
var path = require('path');
var app = express();
var dev = "http://127.0.0.1:8080";
//var dev = "http://wx-dev.seezoon.com/seezoon-framework-all";

var prod = "https://dev.seezoon.com";
var uat = "";
var setting = {
		src:'./src',
		dist:'./dist',
		requestUrlPreffix:dev,
		requestPathPlaceHolder:'${requestPath}$',
}
// 清空输出文件
gulp.task('del', function() {
	return del('dist/**/*');
});
// 复制文件 不包含可以'!src/**'
gulp.task('copy',function() {
	return gulp.src(['src/*/plugins/**','src/*/img/**','src/*/css/**'], { base: setting.src })
	  .pipe(gulp.dest(setting.dist));
});
// html include 处理 @@include('include/header.html')
gulp.task('include',function() {
	return gulp.src(['src/*/pages/**','src/*/js/**'], { base: setting.src })
	.pipe(fileinclude({
          prefix: '@@',
          basepath: '@file'
        }))
    .pipe(replace(setting.requestPathPlaceHolder , setting.requestUrlPreffix))
	.pipe(gulp.dest(setting.dist));
});
gulp.task('watch',function() {
	return gulp.watch(['src/**/*.*','!src/admin/plugins/**']).on("all" , function(path,stats) {
		console.log('File ' + stats + '  running tasks...');
		gulp.src(stats, { base: setting.src })
		.pipe(fileinclude({
			prefix: '@@',
			basepath: '@file'
		}))
		.pipe(replace(setting.requestPathPlaceHolder , setting.requestUrlPreffix))
		.pipe(gulp.dest(setting.dist));
		gulp.series("include");
	});
});
gulp.task('start',function(cb) {
	// 本地测试127.0.0.1 和 local host  有跨域
	app.all('*', function(req, res, next) {
	    res.header("Access-Control-Allow-Origin", "*");
	    res.header('Access-Control-Allow-Methods', 'PUT, GET, POST, DELETE, OPTIONS');
	    res.header("Access-Control-Allow-Headers", "X-Requested-With");
	    res.header('Access-Control-Allow-Headers', 'Content-Type');
	    next();
	});
	app.use(express.static(path.join(__dirname, setting.dist)))
	app.listen(8888, () => {
	  console.log('App listening at port 8888');
	});
	cb();
});

// 默认任务
gulp.task('default', gulp.series('copy','include',gulp.parallel('start','watch')));
gulp.task("env-uat",function(cb){
	setting.requestUrlPreffix = uat;
	cb();
});
gulp.task("env-prod",function(cb){
	setting.requestUrlPreffix = prod;
	cb();
});
gulp.task('uat', gulp.series('del','copy','env-uat','include'));
gulp.task('prod', gulp.series('del','copy','env-prod','include'));

