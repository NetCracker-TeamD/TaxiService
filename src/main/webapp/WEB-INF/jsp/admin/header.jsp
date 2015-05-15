<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                    aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/index">Smart Taxi</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a href="#">Users</a></li>
                <li><a href="#">Groups</a></li>
                <li><a href="/admin/drivers">Drivers</a></li>
                <li><a href="/admin/cars">Cars</a></li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Tariffs
                        <span class="caret"></span></a>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="/admin/tariffs_by_time">Tariffs by time</a></li>
                        <li><a href="/admin/service_tariffs">Service tariffs</a></li>
                        <li><a href="/admin/feature_tariffs">Feature tariffs</a></li>
                        <li><a href="/admin/car_tariffs">Class car tariffs</a></li>
                    </ul>
                </li>
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Reports
                        <span class="caret"></span></a>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="#">Report type 1</a></li>
                        <li><a href="#">Report type 2</a></li>
                        <li><a href="#">Report type 3</a></li>
                    </ul>
                </li>
            </ul>
            <div class="navbar-form navbar-right">
                <button type="button" class="btn btn-primary">Sign out</button>
            </div>
        </div>
    </div>
</nav>