<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${name}</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            line-height: 1.6;
            color: #333;
        }
        header {
            background-color: #4CAF50;
            color: white;
            padding: 10px 0;
            text-align: center;
        }
        nav {
            background-color: #333;
            padding: 10px 0;
            text-align: center;
        }
        nav a {
            color: white;
            margin: 0 15px;
            text-decoration: none;
            font-weight: bold;
        }
        nav a:hover {
            text-decoration: underline;
        }
        .banner {
            background-image: url('https://via.placeholder.com/1200x400');
            background-size: cover;
            background-position: center;
            height: 400px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            text-align: center;
        }
        .banner h1 {
            font-size: 3em;
            margin: 0;
        }
        .content {
            padding: 40px 20px;
            max-width: 1200px;
            margin: 0 auto;
        }
        .content h2 {
            border-bottom: 2px solid #4CAF50;
            padding-bottom: 10px;
        }
        .content p {
            font-size: 1.1em;
        }
        footer {
            background-color: #333;
            color: white;
            text-align: center;
            padding: 20px 0;
            margin-top: 40px;
        }
    </style>
</head>
<body>

<header>
    <h1>${complayName}</h1>
</header>

<nav>
    <a href="#home">首页</a>
    <a href="#about">关于我们</a>
    <a href="#services">服务</a>
    <a href="#contact">联系我们</a>
</nav>

<div class="banner" id="home">
    <h1>欢迎来到公司官网</h1>
</div>

<div class="content" id="about">
    <h2>关于我们</h2>
    <p>我们是一家致力于提供高质量产品和服务的公司。我们的团队由经验丰富的专业人士组成，致力于为客户提供最佳的解决方案。</p>
</div>

<div class="content" id="services">
    <h2>我们的服务</h2>
    <p>我们提供多种服务，包括但不限于：</p>
    <ul>
        <#list menuItems as item>
            <li>${item.label}</li>
        </#list>
    </ul>
</div>

<div class="content" id="contact">
    <h2>联系我们</h2>
    <p>如果您有任何问题或需要进一步的信息，请随时联系我们：</p>
    <p>电话: 123-456-7890</p>
    <p>邮箱: info@company.com</p>
</div>

<footer>
    <p>&copy; 2023 公司名称. 保留所有权利.</p>
</footer>

</body>
</html>