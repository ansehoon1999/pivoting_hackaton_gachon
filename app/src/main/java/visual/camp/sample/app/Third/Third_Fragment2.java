package visual.camp.sample.app.Third;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import visual.camp.sample.app.Book;
import visual.camp.sample.app.R;
import visual.camp.sample.app.RecyclerViewAdapter;


public class Third_Fragment2 extends Fragment {
    List<Book> lstBook ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_third_2, container, false);

        lstBook = new ArrayList<>();
        lstBook.add(new Book("Kongji Patji","Categorie Book","\"Kongji Patji\" is a novel about a story that has been handed down from a long time ago, and the author is not known. The main story is about Kongji, who has a good heart, finding happiness in the bad stepmother and Patji. ",R.drawable.book1));
        lstBook.add(new Book("Who pooed ...","Categorie Book","Description book",R.drawable.book2));
        lstBook.add(new Book("Who moved ...","Categorie Book","Description book",R.drawable.book3));
        lstBook.add(new Book("Snow White","Categorie Book","Description book",R.drawable.book4));
        lstBook.add(new Book("Harry Potter","Categorie Book","Description book",R.drawable.book5));
        lstBook.add(new Book("Little Prince","Categorie Book","Description book",R.drawable.book6));
        lstBook.add(new Book("Pet Problems","Categorie Book","Description book",R.drawable.book7));
        lstBook.add(new Book("Frozen","Categorie Book","Description book",R.drawable.book8));
        lstBook.add(new Book("Hana, be trapped ...","Categorie Book","Description book",R.drawable.book9));
        lstBook.add(new Book("Pages Bookstore","Categorie Book","Description book",R.drawable.book10));
        lstBook.add(new Book("The Vegitarian","Categorie Book","Description book",R.drawable.thevigitarian));
        lstBook.add(new Book("The Wild Robot","Categorie Book","Description book",R.drawable.thewildrobot));
        lstBook.add(new Book("Maria Semples","Categorie Book","Description book",R.drawable.mariasemples));
        lstBook.add(new Book("The Martian","Categorie Book","Description book",R.drawable.themartian));
        lstBook.add(new Book("He Died with...","Categorie Book","Description book",R.drawable.hediedwith));

        RecyclerView myrv = (RecyclerView) rootview.findViewById(R.id.recyclerview_id);
        RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(getContext(),lstBook);
        myrv.setLayoutManager(new GridLayoutManager(getContext(),2));
        myrv.setAdapter(myAdapter);

        return rootview;
    }
}