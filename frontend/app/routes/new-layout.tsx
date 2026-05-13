import { Outlet } from "react-router";
import Navbar from "~/components/navbar";
import GlobalSpinner from "~/components/Spinner";

export default function NewLayout() {
  return (
    <>
      <GlobalSpinner />
      <div className="row newsfeed-size">
        <div className="col-md-12 newsfeed-right-side">
          <Navbar />
          <div className="row newsfeed-right-side-content mt-3">
            <Outlet />
          </div>
        </div>
      </div>
    </>
  );
}
