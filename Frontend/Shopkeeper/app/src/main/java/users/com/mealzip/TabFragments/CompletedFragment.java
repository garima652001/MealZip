package users.com.mealzip.TabFragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;

import java.util.ArrayList;
import java.util.List;

import users.com.mealzip.Adapter.OrderAdapter;
import users.com.mealzip.Models.IdModel;
import users.com.mealzip.Models.Orderarray;
import users.com.mealzip.Models.OrdersModel;
import users.com.mealzip.R;
import users.com.mealzip.ViewModels.OrderViewModel;

public class CompletedFragment extends Fragment {
    Context context;
    OrderViewModel orderViewModel;
    OrderAdapter orderAdapter;
    RecyclerView recyclerView;
    ConstraintLayout constraintLayout;
    LinearLayout linearLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_completed, container, false);
        constraintLayout = view.findViewById(R.id.cl1);
        linearLayout= view.findViewById(R.id.no_order);
        recyclerView = view.findViewById(R.id.recycler_view1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        loaddata();
        return view;
    }

    private void loaddata() {
        orderViewModel = ViewModelProviders.of((FragmentActivity) context).get(OrderViewModel.class);
        orderViewModel.getFeed(context).observe(getViewLifecycleOwner(), new Observer<OrdersModel>() {
            @Override
            public void onChanged(@Nullable OrdersModel orderList) {

                List<Orderarray> filteredList = new ArrayList<>();
                List<Orderarray> list = new ArrayList<>();
                List<String> idlist = new ArrayList<>();
                List<IdModel> finalList = new ArrayList<>();
                for (int i = 0; i < orderList.getOrders().size(); i++) {
                    String id = orderList.getOrders().get(i).get_id();
                    filteredList = Stream.of(orderList.getOrders().get(i).getOrdersArray()).filter(new Predicate<Orderarray>() {
                        @Override
                        public boolean test(Orderarray item) {
                            return item.getOrderStatus().equalsIgnoreCase("Delivered");
                        }
                    }).collect(Collectors.toList());
                    list.addAll(filteredList);
                    for (int j = 0; j < filteredList.size(); j++) {
                        idlist.add(id);
                    }
                }
                for (int i = 0; i < idlist.size(); i++) {
                    IdModel idmodel = new IdModel(list.get(i).getIsaccepted(), list.get(i).getOrderStatus(), list.get(i).getPaid(), list.get(i).get_id(), list.get(i).getItemName(), list.get(i).getItemId(), list.get(i).getPrice(), list.get(i).getRating(), idlist.get(i));
                    finalList.add(idmodel);
                }
                if (finalList.size() != 0) {
                    constraintLayout.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.GONE);
                    orderAdapter = new OrderAdapter(finalList, context);
                    recyclerView.setAdapter(orderAdapter);
                    // Toast.makeText(context, "Food is: "+orderList.getOrders().get(0).getItemName(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}